package com.example.node_project

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil

@Suppress("DEPRECATION")
class PlanList2ViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext
    private val repository = PlanList2Repository()

    val defaultLocation = LatLng(37.60153324458494, 126.86503171920776)
    private val _markers = MutableLiveData<MutableMap<String, Marker>>(mutableMapOf())
    val markers: LiveData<MutableMap<String, Marker>> = _markers
    private val _selectedMarker = MutableLiveData<Marker?>()
    private val _searchedLocation = MutableLiveData<LatLng?>()
    val searchedLocation: LiveData<LatLng?> = _searchedLocation
    private val _searchedTitle = MutableLiveData<String?>()
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private lateinit var googleMap: GoogleMap

    // GoogleMap 객체 설정
    fun setGoogleMap(map: GoogleMap) {
        googleMap = map
    }

    // 장소 검색 함수
    fun searchPlace(query: String, editText: EditText) {
        val geoCoder = Geocoder(context)
        val results = geoCoder.getFromLocationName(query, 1)
        if (!results.isNullOrEmpty()) {
            val location = results[0]
            _searchedLocation.value = LatLng(location.latitude, location.longitude)
            _searchedTitle.value = query
            editText.text.clear()
        } else {
            showCustomToast("장소를 찾을 수 없습니다.", 500)
        }
    }

    // 사용자 정의 Toast 함수
    private fun showCustomToast(message: String, duration: Long) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
        toast.view?.postDelayed({ toast.cancel() }, duration)
    }

    // 마커 추가 함수
    fun addMarker() {
        val location = _searchedLocation.value
        val title = _searchedTitle.value
        if (location != null && title != null) {
            addMarkerAtLocation(location, title)
            repository.saveMarker(title, location.latitude, location.longitude,
                { showCustomToast("$title 마커가 추가되었습니다.", 500) },
                { message -> showCustomToast(message, 500) }
            )
            _searchedLocation.value = null
            _searchedTitle.value = null
        } else {
            showCustomToast("추가할 마커가 없습니다.", 500)
        }
    }

    // 선택된 마커 삭제 함수
    fun deleteSelectedMarker() {
        _selectedMarker.value?.let { marker ->
            val markerTitle = marker.title ?: return@let

            // Firebase에서 마커 삭제
            repository.deleteMarker(markerTitle,
                {
                    // 마커가 성공적으로 삭제된 후 Google Map에서 제거
                    removeMarker(markerTitle)
                    showCustomToast("$markerTitle 마커가 삭제되었습니다.", 500)
                },
                { message -> showCustomToast(message, 500) }
            )

            // 선택된 마커 초기화
            _selectedMarker.value = null
        } ?: run {
            showCustomToast("선택된 마커가 없습니다.", 500)
        }
    }

    // 마커 클릭 이벤트 처리 함수
    @SuppressLint("DefaultLocale")
    fun onMarkerClick(marker: Marker): Boolean {
        _selectedMarker.value = marker
        val distance = SphericalUtil.computeDistanceBetween(defaultLocation, marker.position)
        val distanceInKm = distance / 1000
        showCustomToast("${marker.title} 선택됨. 항공대와의 거리: ${String.format("%.2f", distanceInKm)} km", 500)
        return true
    }

    // 특정 위치에 마커 추가 함수
    private fun addMarkerAtLocation(location: LatLng, title: String) {
        val markerOptions = MarkerOptions()
            .position(location)
            .title(title)
            .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(title)))
        val marker = googleMap.addMarker(markerOptions)
        val newMarkers = _markers.value ?: mutableMapOf()
        if (marker != null) {
            newMarkers[title] = marker
            _markers.value = newMarkers
        }
    }

    // 마커 제거 함수
    private fun removeMarker(title: String) {
        val currentMarkers = _markers.value ?: return
        val markerToRemove = currentMarkers[title]
        if (markerToRemove != null) {
            // Google Map에서 마커 제거
            markerToRemove.remove()

            // LiveData에서 마커 제거
            currentMarkers.remove(title)
            _markers.postValue(currentMarkers) // LiveData 업데이트
        }
    }

    // 저장된 장소를 로드하는 함수
    fun loadSavedPlaces() {
        repository.loadSavedPlaces(
            { markers ->
                // Google Map 초기화
                googleMap.clear() // 기존 마커 제거
                // 마커를 추가하기 위한 맵 생성
                val newMarkers = mutableMapOf<String, Marker>()
                markers.forEach { markerItem ->
                    val markerOptions = MarkerOptions()
                        .position(LatLng(markerItem.latitude, markerItem.longitude))
                        .title(markerItem.title)
                        .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(markerItem.title)))

                    // 마커를 추가하고 null 체크
                    val marker = googleMap.addMarker(markerOptions)
                    if (marker != null) {
                        newMarkers[markerItem.title] = marker
                    }
                }
                _markers.value = newMarkers // LiveData 업데이트
            },
            { message -> showCustomToast(message, 500) }
        )
    }

    // 커스텀 마커 생성 함수
    @SuppressLint("InflateParams")
    private fun createCustomMarker(title: String): Bitmap {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_marker, null)
        val markerTextView = view.findViewById<TextView>(R.id.markerTextView)
        markerTextView.text = title
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }
}
