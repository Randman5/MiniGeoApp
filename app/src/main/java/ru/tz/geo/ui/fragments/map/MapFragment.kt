package ru.tz.geo.ui.fragments.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.PedestrianRouter
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.Session.RouteListener
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import ru.tz.geo.R
import ru.tz.geo.databinding.FragmentMapBinding
import kotlin.system.exitProcess


class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding

    private val viewModel: MapFragmentViewModel by viewModels()

    private var routeStartLocation = Point(0.0, 0.0)

    private var userLocationLayer: UserLocationLayer? = null
    private lateinit var pedestrianRouter: PedestrianRouter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            createUserLocationLayer()
        } else {
            showNoAccessGeoDialog()
        }
    }
    private val tapListener: MapObjectTapListener = MapObjectTapListener { obj, point ->
        when (obj) {
            is PlacemarkMapObject -> viewModel.showMarkerDialog(point, obj)
        }
        true
    }
    private val userLocationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(userLocationView: UserLocationView) {
            userLocationView.arrow.setIcon(
                ImageProvider.fromResource(
                    requireContext(),
                    R.drawable.ic_nav_arrow
                )
            )
            val pinIcon = userLocationView.pin.useCompositeIcon()
            pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(requireContext(), R.drawable.disabled),
                IconStyle().setAnchor(PointF(0.5f, 0.5f))
                    .setRotationType(RotationType.ROTATE)
                    .setZIndex(1f)
                    .setScale(0.5f)
            )
        }

        override fun onObjectRemoved(p0: UserLocationView) {}

        override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}
    }
    private val inputListener = object : InputListener {
        override fun onMapTap(p0: Map, p1: Point) {}

        override fun onMapLongTap(map: Map, point: Point) {
            viewModel.showCreateMarkerDialog(point)
        }
    }
    private val routeListener = object : RouteListener {
        override fun onMasstransitRoutes(routes: MutableList<Route>) {
            routes.forEach { route ->
                val polyline = binding.mapview.map.mapObjects.addPolyline().apply {
                    geometry = route.geometry
                }
                viewModel.setCurrentRoute(polyline)
            }
        }

        override fun onMasstransitRoutesError(p0: Error) {
            Toast(requireContext()).apply {
                setText(p0.toString())
            }.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        MapKitFactory.setApiKey("ccab25b7-5ca7-4368-9405-534e468e87cf")
        MapKitFactory.initialize(requireContext())

        binding = FragmentMapBinding.inflate(layoutInflater)
        pedestrianRouter = TransportFactory.getInstance().createPedestrianRouter()
        binding.fab.setOnClickListener {
            cameraUserPosition()
        }

        viewModel.anchorToUser.observe(viewLifecycleOwner) { isAnchor ->
            anchorToUser(isAnchor)
        }

        viewModel.showMarkerDialog.observe(viewLifecycleOwner) { pointObject ->
            showMarkerDialog(pointObject.first, pointObject.second)
        }

        viewModel.showCreateMarkerDialog.observe(viewLifecycleOwner) {
            showCreatePlacemarkDialog(it)
        }

        viewModel.addPlaceMark.observe(viewLifecycleOwner) {
            addMarker(it.point, it.text)
        }

        viewModel.addPlaceMarks.observe(viewLifecycleOwner) { markers ->
            markers.forEach {
                addMarker(it.point, it.text)
            }
        }

        viewModel.routeEndPoint.observe(viewLifecycleOwner) { point ->
            createRoute(point)
        }

        viewModel.currentRouteRemove.observe(viewLifecycleOwner) {
            binding.mapview.map.mapObjects.remove(it)
        }

        viewModel.deleteMarker.observe(viewLifecycleOwner) {
            binding.mapview.map.mapObjects.remove(it)
        }

        binding.fab.setOnLongClickListener {
            viewModel.changeUserAnchor()
        }

        binding.mapview.map.addInputListener(inputListener)


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun createUserLocationLayer() {
        userLocationLayer =
            MapKitFactory.getInstance().createUserLocationLayer(binding.mapview.mapWindow).also {
                it.isVisible = true
                it.isHeadingEnabled = true
            }
        userLocationLayer?.setObjectListener(userLocationObjectListener)
    }

    private fun addMarker(point: Point, text: String) {
        binding.mapview.map.mapObjects.addPlacemark().apply {
            geometry = point
            setIcon(ImageProvider.fromResource(requireContext(), R.drawable.marker_view))
            setIconStyle(IconStyle().apply {
                anchor = PointF(0.5f, 0.8f)
            })
            addTapListener(tapListener)
            setText(text)
            userData = text
        }
    }

    private fun cameraUserPosition() {
        if (userLocationLayer?.cameraPosition() != null) {
            routeStartLocation = userLocationLayer?.cameraPosition()!!.target
            binding.mapview.map.move(
                CameraPosition(routeStartLocation, 16f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        } else {
            binding.mapview.map.move(CameraPosition(Point(0.0, 0.0), 16f, 0f, 0f))
        }
    }

    private fun anchorToUser(isAnchor: Boolean) {
        if (isAnchor) {
            binding.fab.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.nav_button_active
            )
            userLocationLayer?.setAnchor(
                PointF(
                    (binding.mapview.width * 0.5).toFloat(),
                    (binding.mapview.height * 0.5).toFloat()
                ),
                PointF(
                    (binding.mapview.width * 0.5).toFloat(),
                    (binding.mapview.height * 0.83).toFloat()
                )
            )
        } else {
            binding.fab.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.nav_button)
            userLocationLayer?.resetAnchor()
        }
    }

    private fun createRoute(point: Point) {
        val routes = arrayListOf<RequestPoint>()
        routes.add(
            RequestPoint(
                userLocationLayer?.cameraPosition()!!.target,
                RequestPointType.VIAPOINT,
                null,
                null
            )
        )
        routes.add(RequestPoint(point, RequestPointType.WAYPOINT, null, null))
        viewModel.removeCurrentRoute()
        pedestrianRouter.requestRoutes(routes, TimeOptions(null, null), false, routeListener)
    }

    private fun showCreatePlacemarkDialog(point: Point) {
        val textInputLayout = TextInputLayout(requireContext())
        textInputLayout.setPadding(
            resources.getDimensionPixelOffset(R.dimen.dp_19),
            0,
            resources.getDimensionPixelOffset(R.dimen.dp_19),
            0
        )
        val input = EditText(context)
        textInputLayout.hint = getString(R.string.marker_name_hint)
        textInputLayout.addView(input)
        val alert = AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle(getString(R.string.marker_name_input_dialog))
            .setView(textInputLayout)
            .setPositiveButton(getString(R.string.add)) { dialog, _ ->
                val str = input.text.toString()
                if (str.isNotBlank()) {
                    viewModel.savePlacemark(point, str)
                }
                dialog.cancel()
            }
            .setNegativeButton(getString(R.string.back)) { dialog, _ ->
                dialog.cancel()
            }.create()
        alert.show()
    }

    private fun showMarkerDialog(point: Point, obj: PlacemarkMapObject) {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(getString(R.string.create_route)) { dialog, _ ->
                viewModel.setRoute(point, obj)
                dialog.cancel()
            }
            .setNegativeButton(getString(R.string.delete)) { dialog, _ ->
                viewModel.deleteMarker(obj)
                dialog.cancel()
            }
            .create()
            .show()
    }

    private fun showNoAccessGeoDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.No_access_to_geolocation))
            .setPositiveButton(getString(R.string.re_request_access)) { dialog, _ ->
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                dialog.cancel()
            }
            .setNegativeButton(getString(R.string.Exit)) { dialog, _ ->
                exitProcess(0)
            }
            .create()
            .show()
    }

    fun checkGPS() {
        val manager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertMessageNoGps()
        }
    }

    private fun showAlertMessageNoGps() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.GPS_no_enabled))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.GPS_on) ) { dialog, id ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton( getString(R.string.Exit) ) { dialog, id ->
                exitProcess(0)
            }
        val alert = builder.create()
        alert.show()
    }

    override fun onResume() {
        super.onResume()
        checkGPS()
    }

}