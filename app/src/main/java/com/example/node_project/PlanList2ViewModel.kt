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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import com.google.firebase.database.*
import java.util.Locale

@Suppress("DEPRECATION")
class PlanList2ViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext

    // 기본 위치 설정 (한국항공대학교 위치)
    val defaultLocation = LatLng(37.60153324458494, 126.86503171920776)

    // 마커 목록을 위한 LiveData
    private val _markers = MutableLiveData<MutableMap<String, Marker>>(mutableMapOf())
    val markers: LiveData<MutableMap<String, Marker>> = _markers

    // 선택된 마커를 위한 LiveData
    private val _selectedMarker = MutableLiveData<Marker?>()

    // 검색된 위치와 제목을 위한 LiveData
    private val _searchedLocation = MutableLiveData<LatLng?>()
    val searchedLocation: LiveData<LatLng?> = _searchedLocation
    private val _searchedTitle = MutableLiveData<String?>()

    // 토스트 메시지를 위한 LiveData
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private lateinit var googleMap: GoogleMap
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("markers")

    // GoogleMap 객체 설정
    fun setGoogleMap(map: GoogleMap) {
        googleMap = map
    }

    // 장소 검색 함수
    fun searchPlace(query: String, editText: EditText) { // EditText를 파라미터로 추가
        val geoCoder = Geocoder(context, Locale.getDefault())
        val results = geoCoder.getFromLocationName(query, 1)
        if (!results.isNullOrEmpty()) {
            val location = results[0]
            val latitude = location.latitude
            val longitude = location.longitude
            _searchedLocation.value = LatLng(latitude, longitude)
            _searchedTitle.value = query
            _toastMessage.value = "$query 위치가 검색되었습니다. '마커추가' 버튼을 누르세요."

            // 검색 후 검색창 초기화
            editText.text.clear() // EditText 초기화
        } else {
            _toastMessage.value = "장소를 찾을 수 없습니다."
        }
    }

    // 마커 추가 함수 (Firebase에 추가)
    fun addMarker() {
        val location = _searchedLocation.value
        val title = _searchedTitle.value
        if (location != null && title != null) {
            addMarkerAtLocation(location, title)
            saveMarkerToFirebase(title, location.latitude, location.longitude) // Firebase에 저장
            _toastMessage.value = "$title 마커가 추가되었습니다."
            _searchedLocation.value = null
            _searchedTitle.value = null
        } else {
            _toastMessage.value = "추가할 마커가 없습니다."
        }
    }

    // 마커를 Firebase에 저장하는 메서드
    private fun saveMarkerToFirebase(title: String, latitude: Double, longitude: Double) {
        val marker = MarkerData(title, latitude, longitude)
        databaseReference.child(title).setValue(marker).addOnSuccessListener {
            // 성공적으로 저장됨
        }.addOnFailureListener {
            _toastMessage.value = "마커 저장에 실패했습니다."
        }
    }

    // 선택된 마커 삭제 함수 (Firebase에서 삭제)
    fun deleteSelectedMarker() {
        _selectedMarker.value?.let { marker ->
            val markerTitle = marker.title ?: return@let
            removeMarker(markerTitle) // 지도 및 LiveData에서 제거
            deleteMarkerFromFirebase(markerTitle) // Firebase에서 제거
            _selectedMarker.value = null // 선택된 마커 초기화
        } ?: run {
            _toastMessage.value = "선택된 마커가 없습니다."
        }
    }

    // Firebase에서 마커를 삭제하는 메서드
    private fun deleteMarkerFromFirebase(title: String) {
        databaseReference.child(title).removeValue().addOnSuccessListener {
            // Firebase에서 성공적으로 삭제됨
        }.addOnFailureListener {
            _toastMessage.value = "마커 삭제에 실패했습니다."
        }
    }

    // 마커 클릭 이벤트 처리 함수
    @SuppressLint("DefaultLocale")
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
        val markerToRemove = currentMarkers[title]
        if (markerToRemove != null) {
            markerToRemove.remove() // 지도에서 마커 제거
            currentMarkers.remove(title) // LiveData에서 마커 제거
            _markers.postValue(currentMarkers) // LiveData 업데이트
            _toastMessage.value = "$title 마커가 삭제되었습니다."
        } else {
            _toastMessage.value = "$title 마커를 찾을 수 없습니다."
        }
    }

    // 저장된 마커 불러오기
    fun loadSavedPlaces() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newMarkers = mutableMapOf<String, Marker>() // 새로운 마커 맵 생성
                googleMap.clear() // 지도 초기화 (기존 마커 제거)
                for (dataSnapshot in snapshot.children) {
                    val markerData = dataSnapshot.getValue(MarkerData::class.java)
                    markerData?.let { data ->
                        val location = LatLng(data.latitude, data.longitude)

                        // 커스텀 마커 생성 및 적용
                        val customIcon = BitmapDescriptorFactory.fromBitmap(createCustomMarker(data.title))
                        val markerOptions = MarkerOptions()
                            .position(location)
                            .title(data.title)
                            .icon(customIcon) // 커스텀 아이콘 설정

                        val marker = googleMap.addMarker(markerOptions)
                        if (marker != null) {
                            newMarkers[data.title] = marker
                        }
                    }
                }
                _markers.postValue(newMarkers) // LiveData 업데이트
            }

            override fun onCancelled(error: DatabaseError) {
                _toastMessage.value = "마커 로드에 실패했습니다."
            }
        })
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

    // MarkerOptions를 Marker로 변환하는 확장 함수
    private fun MarkerOptions.toMarker(map: GoogleMap): Marker {
        return map.addMarker(this) ?: throw IllegalStateException("Failed to add marker to map")
    }
}

// 마커 정보를 위한 데이터 클래스 추가
data class MarkerData(
    val title: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
