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
// 장소 및 마커 정보를 관리하는 ViewModel 클래스
class PlanList2ViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext // Context를 안전하게 가져옴
    private val repository = PlanList2Repository() // Repository 인스턴스 생성

    // 기본 위치와 관련된 변수
    val defaultLocation = LatLng(37.60153324458494, 126.86503171920776) // 기본 위치 (예: 항공대)

    // 마커 목록을 저장하기 위한 LiveData
    private val _markers = MutableLiveData<MutableMap<String, Marker>>(mutableMapOf()) // 현재 마커 목록
    val markers: LiveData<MutableMap<String, Marker>> = _markers // 외부에서 접근 가능한 마커 목록

    private val _selectedMarker = MutableLiveData<Marker?>() // 현재 선택된 마커
    private val _searchedLocation = MutableLiveData<LatLng?>() // 검색된 위치
    val searchedLocation: LiveData<LatLng?> = _searchedLocation // 외부에서 접근 가능한 검색된 위치
    private val _searchedTitle = MutableLiveData<String?>() // 검색된 제목
    private val _toastMessage = MutableLiveData<String>() // Toast 메시지
    val toastMessage: LiveData<String> = _toastMessage // 외부에서 접근 가능한 Toast 메시지

    private lateinit var googleMap: GoogleMap // GoogleMap 객체

    // GoogleMap 객체 설정
    fun setGoogleMap(map: GoogleMap) {
        googleMap = map // ViewModel에 GoogleMap 설정
    }

    // 장소 검색 함수
    fun searchPlace(query: String, editText: EditText) {
        val geoCoder = Geocoder(context) // Geocoder 인스턴스 생성
        val results = geoCoder.getFromLocationName(query, 1) // 장소 검색
        if (!results.isNullOrEmpty()) { // 검색 결과가 있을 경우
            val location = results[0] // 첫 번째 결과 가져오기
            _searchedLocation.value = LatLng(location.latitude, location.longitude) // 검색된 위치 저장
            _searchedTitle.value = query // 검색된 제목 저장
            editText.text.clear() // 입력 필드 초기화
        } else {
            showCustomToast("장소를 찾을 수 없습니다.", 500) // 검색 실패 메시지 표시
        }
    }

    // 사용자 정의 Toast 함수
    private fun showCustomToast(message: String, duration: Long) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT) // Toast 메시지 생성
        toast.show() // Toast 표시
        toast.view?.postDelayed({ toast.cancel() }, duration) // 지정된 시간 후 Toast 제거
    }

    // 마커 추가 함수
    fun addMarker() {
        val location = _searchedLocation.value // 검색된 위치 가져오기
        val title = _searchedTitle.value // 검색된 제목 가져오기
        if (location != null && title != null) { // 위치와 제목이 모두 존재할 경우
            addMarkerAtLocation(location, title) // 위치에 마커 추가
            repository.saveMarker(title, location.latitude, location.longitude,
                { showCustomToast("$title 마커가 추가되었습니다.", 500) }, // 성공 시 메시지 표시
                { message -> showCustomToast(message, 500) } // 실패 시 에러 메시지 표시
            )
            _searchedLocation.value = null // 검색된 위치 초기화
            _searchedTitle.value = null // 검색된 제목 초기화
        } else {
            showCustomToast("추가할 마커가 없습니다.", 500) // 마커 추가 불가 메시지 표시
        }
    }

    // 선택된 마커 삭제 함수
    fun deleteSelectedMarker() {
        _selectedMarker.value?.let { marker ->
            val markerTitle = marker.title ?: return@let // 선택된 마커의 제목 가져오기

            // Firebase에서 마커 삭제
            repository.deleteMarker(markerTitle,
                {
                    // 마커가 성공적으로 삭제된 후 Google Map에서 제거
                    removeMarker(markerTitle)
                    showCustomToast("$markerTitle 마커가 삭제되었습니다.", 500) // 삭제 성공 메시지 표시
                },
                { message -> showCustomToast(message, 500) } // 실패 시 에러 메시지 표시
            )

            // 선택된 마커 초기화
            _selectedMarker.value = null
        } ?: run {
            showCustomToast("선택된 마커가 없습니다.", 500) // 선택된 마커가 없을 경우 메시지 표시
        }
    }

    // 마커 클릭 이벤트 처리 함수
    @SuppressLint("DefaultLocale")
    fun onMarkerClick(marker: Marker): Boolean {
        _selectedMarker.value = marker // 선택된 마커 저장
        val distance = SphericalUtil.computeDistanceBetween(defaultLocation, marker.position) // 기본 위치와 마커 간 거리 계산
        val distanceInKm = distance / 1000 // 거리를 킬로미터로 변환
        showCustomToast("${marker.title} 선택됨. 항공대와의 거리: ${String.format("%.2f", distanceInKm)} km", 500) // 거리 정보 표시
        return true // 클릭 이벤트 처리 완료
    }

    // 특정 위치에 마커 추가 함수
    private fun addMarkerAtLocation(location: LatLng, title: String) {
        val markerOptions = MarkerOptions()
            .position(location) // 위치 설정
            .title(title) // 제목 설정
            .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(title))) // 커스텀 마커 아이콘 설정
        val marker = googleMap.addMarker(markerOptions) // Google Map에 마커 추가
        val newMarkers = _markers.value ?: mutableMapOf() // 현재 마커 목록 가져오기
        if (marker != null) {
            newMarkers[title] = marker // 새 마커 목록에 추가
            _markers.value = newMarkers // LiveData 업데이트
        }
    }

    // 마커 제거 함수
    private fun removeMarker(title: String) {
        val currentMarkers = _markers.value ?: return // 현재 마커 목록 가져오기
        val markerToRemove = currentMarkers[title] // 제거할 마커 찾기
        if (markerToRemove != null) {
            // Google Map에서 마커 제거
            markerToRemove.remove()

            // LiveData에서 마커 제거
            currentMarkers.remove(title) // 마커 목록에서 제거
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
                val newMarkers = mutableMapOf<String, Marker>() // 새 마커 목록 생성
                markers.forEach { markerItem ->
                    val markerOptions = MarkerOptions()
                        .position(LatLng(markerItem.latitude, markerItem.longitude)) // 마커 위치 설정
                        .title(markerItem.title) // 마커 제목 설정
                        .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(markerItem.title))) // 커스텀 마커 아이콘 설정

                    // 마커를 추가하고 null 체크
                    val marker = googleMap.addMarker(markerOptions) // Google Map에 마커 추가
                    if (marker != null) {
                        newMarkers[markerItem.title] = marker // 마커 목록에 추가
                    }
                }
                _markers.value = newMarkers // LiveData 업데이트
            },
            { message -> showCustomToast(message, 500) } // 실패 시 에러 메시지 표시
        )
    }

    // 커스텀 마커 생성 함수
    @SuppressLint("InflateParams")
    private fun createCustomMarker(title: String): Bitmap {
        // 커스텀 마커 뷰 생성
        val view = LayoutInflater.from(context).inflate(R.layout.custom_marker, null) // XML 레이아웃 인플레이트
        val markerTextView = view.findViewById<TextView>(R.id.markerTextView) // 커스텀 마커의 텍스트 뷰
        markerTextView.text = title // 마커 제목 설정
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED) // 뷰 측정
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888) // 비트맵 생성
        val canvas = Canvas(bitmap) // 캔버스 생성
        view.layout(0, 0, view.measuredWidth, view.measuredHeight) // 뷰 레이아웃 설정
        view.draw(canvas) // 뷰 그리기
        return bitmap // 생성된 비트맵 반환
    }
}
