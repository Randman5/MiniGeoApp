package ru.tz.geo.ui.fragments.map

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolylineMapObject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.tz.geo.App
import ru.tz.geo.data.yandexMapData.dto.MarkerDto
import ru.tz.geo.domain.usecase.mapMarkersUseCases.AddMarkerUseCase
import ru.tz.geo.domain.usecase.mapMarkersUseCases.DeleteMarkerUseCase
import ru.tz.geo.domain.usecase.mapMarkersUseCases.GetMarkersUseCase
import ru.tz.geo.utils.equal
import javax.inject.Inject

class MapFragmentViewModel : ViewModel() {

    private val TAG = MapFragment::class.simpleName
    private var currentRoute: PolylineMapObject? = null
    private var endPointObject: PlacemarkMapObject? = null

    val anchorToUser = MutableLiveData(false)
    val showMarkerDialog = MutableLiveData<Pair<Point, PlacemarkMapObject>>()
    val showCreateMarkerDialog = MutableLiveData<Point>()
    val addPlaceMark = MutableLiveData<MarkerDto>()
    val addPlaceMarks = MutableLiveData<List<MarkerDto>>()
    val deleteMarker = MutableLiveData<PlacemarkMapObject>()
    val currentRouteRemove = MutableLiveData<PolylineMapObject>()
    val routeEndPoint = MutableLiveData<Point>()

    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject
    lateinit var getMarkersUseCase: GetMarkersUseCase

    @Inject
    lateinit var addMarkerUseCase: AddMarkerUseCase

    @Inject
    lateinit var deleteMarkerUseCase: DeleteMarkerUseCase

    init {
        App.get().appComponent.inject(this)
        getMarkers()
        val l = 1
    }


    fun changeUserAnchor(): Boolean {
        anchorToUser.postValue(!anchorToUser.value!!)
        return anchorToUser.value!!
    }

    fun showMarkerDialog(point: Point, obj: PlacemarkMapObject) {
        showMarkerDialog.postValue(Pair(point, obj))
    }

    fun showCreateMarkerDialog(point: Point) {
        showCreateMarkerDialog.postValue(point)
    }

    fun setRoute(point: Point, obj: PlacemarkMapObject) {
        routeEndPoint.postValue(point)
        endPointObject = obj
    }

    fun setCurrentRoute(polylineMapObject: PolylineMapObject) {
        currentRoute = polylineMapObject
    }

    fun removeCurrentRoute() {
        currentRoute?.let {
            currentRouteRemove.postValue(it)
            currentRoute = null
        }
    }

    fun deleteMarker(marker: PlacemarkMapObject) {
        val deletePoint = marker.geometry
        endPointObject?.geometry?.let {
            if (deletePoint.equal(it)) {
                removeCurrentRoute()
            }
        }
        compositeDisposable.add(
            Single.fromCallable {
                deleteMarkerUseCase.execute(deletePoint)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    deleteMarker.postValue(marker)
                }, {
                    Log.e(TAG, "getMarkers: ${it.message}")
                })
        )
    }

    private fun getMarkers() {
        compositeDisposable.add(
            Single.fromCallable {
                getMarkersUseCase.execute()
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    addPlaceMarks.postValue(it)
                }, {
                    Log.e(TAG, "getMarkers: ${it.message}")
                })
        )
    }

    fun savePlacemark(point: Point, text: String) {
        compositeDisposable.add(
            Single.fromCallable {
                addMarkerUseCase.execute(point, text)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    addPlaceMark.postValue(it)
                }, {
                    Log.e(TAG, "getMarkers: ${it.message}")
                })
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}
