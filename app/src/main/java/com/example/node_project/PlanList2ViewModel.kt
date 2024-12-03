package com.example.node_project

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import java.util.Locale

@Suppress("DEPRECATION")
class PlanList2ViewModel(application: Application) : AndroidViewModel(application) {
    // Context 참조를 위한 변수 (메모리 누수 방지를 위해 @SuppressLint 사용)
    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext

    // 기본 위치 설정 (한국항공대학교 위치)
    val defaultLocation = LatLng(37.60153324458494, 126.86503171920776)

    // 마커 목록을 위한 LiveData
    private val _markers = MutableLiveData<MutableMap<String, Marker>>(mutableMapOf())
    val markers: LiveData<MutableMap<String, Marker>> = _markers

    // 선택된 마커를 위한 LiveData
    private val _selectedMarker = MutableLiveData<Marker?>()
    val selectedMarker: LiveData<Marker?> = _selectedMarker

    // 검색된 위치와 제목을 위한 LiveData
    private val _searchedLocation = MutableLiveData<LatLng?>()
    val searchedLocation: LiveData<LatLng?> = _searchedLocation
    private val _searchedTitle = MutableLiveData<String?>()
    val searchedTitle: LiveData<String?> = _searchedTitle

    // 토스트 메시지를 위한 LiveData
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private lateinit var googleMap: GoogleMap

    // GoogleMap 객체 설정
    fun setGoogleMap(map: GoogleMap) {
        googleMap = map
    }

    // 장소 검색 함수
    fun searchPlace(query: String) {
        val geoCoder = Geocoder(context, Locale.getDefault())
        val results = geoCoder.getFromLocationName(query, 1)
        if (!results.isNullOrEmpty()) {
            val location = results[0]
            val latitude = location.latitude
            val longitude = location.longitude
            _searchedLocation.value = LatLng(latitude, longitude)
            _searchedTitle.value = query
            _toastMessage.value = "$query 위치가 검색되었습니다. '마커추가' 버튼을 누르세요."
        } else {
            _toastMessage.value = "장소를 찾을 수 없습니다."
        }
    }

    // 마커 추가 함수
    fun addMarker() {
        val location = _searchedLocation.value
        val title = _searchedTitle.value
        if (location != null && title != null) {
            addMarkerAtLocation(location, title)
            saveToPreferences(location.latitude, location.longitude, title)
            _toastMessage.value = "$title 마커가 추가되었습니다."
            _searchedLocation.value = null
            _searchedTitle.value = null
        } else {
            _toastMessage.value = "추가할 마커가 없습니다."
        }
    }

    // 선택된 마커 삭제 함수
    fun deleteSelectedMarker() {
        _selectedMarker.value?.let {
            removeMarker(it.title ?: "")
            _selectedMarker.value = null
        } ?: run {
            _toastMessage.value = "선택된 마커가 없습니다."
        }
    }

    // 마커 클릭 이벤트 처리 함수
    fun onMarkerClick(marker: Marker): Boolean {
        _selectedMarker.value = marker
        val distance = SphericalUtil.computeDistanceBetween(defaultLocation, marker.position)
        val distanceInKm = distance / 1000
        _toastMessage.value = "${marker.title} 마커가 선택됨. 항공대와의 거리: ${String.format("%.2f", distanceInKm)} km"
        return true
    }

    // 특정 위치에 마커 추가 함수
    private fun addMarkerAtLocation(location: LatLng, title: String) {
        val markerOptions = MarkerOptions()
            .position(location)
            .title(title)
            .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(title)))
        val marker = markerOptions.toMarker(googleMap)
        val newMarkers = _markers.value ?: mutableMapOf()
        newMarkers[title] = marker
        _markers.value = newMarkers
    }

    // 마커 제거 함수
    private fun removeMarker(title: String) {
        val currentMarkers = _markers.value ?: return
        currentMarkers.entries.find { it.key.equals(title, ignoreCase = true) }?.let { entry ->
            entry.value.remove()
            currentMarkers.remove(entry.key)
            removeFromPreferences(title)
            _toastMessage.value = "$title 마커가 삭제되었습니다."
            _markers.value = currentMarkers
        } ?: run {
            _toastMessage.value = "$title 마커를 찾을 수 없습니다."
        }
    }

    // SharedPreferences에 마커 정보 저장
    private fun saveToPreferences(latitude: Double, longitude: Double, query: String) {
        val sharedPreferences = context.getSharedPreferences("SavedPlaces", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(query, "$latitude,$longitude")
            apply()
        }
    }

    // SharedPreferences에서 마커 정보 제거
    private fun removeFromPreferences(title: String) {
        val sharedPreferences = context.getSharedPreferences("SavedPlaces", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove(title)
            apply()
        }
    }

    // 저장된 마커 불러오기
    fun loadSavedPlaces() {
        val sharedPreferences = context.getSharedPreferences("SavedPlaces", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        allEntries.forEach { (key, value) ->
            val coordinates = value.toString().split(",")
            if (coordinates.size == 2) {
                val latitude = coordinates[0].toDoubleOrNull()
                val longitude = coordinates[1].toDoubleOrNull()
                if (latitude != null && longitude != null) {
                    addMarkerAtLocation(LatLng(latitude, longitude), key)
                }
            }
        }
    }

    // 커스텀 마커 생성 함수
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

    // MarkerOptions를 Marker로 변환하는 확장 함수
    private fun MarkerOptions.toMarker(map: GoogleMap): Marker {
        return map.addMarker(this) ?: throw IllegalStateException("Failed to add marker to map")
    }
}
