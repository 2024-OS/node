package com.example.node_project

import com.google.firebase.database.*

// Firebase와의 데이터 상호작용을 관리하는 클래스
class PlanList2Repository {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("markers") // "markers" 경로 참조

    // 마커 저장
    fun saveMarker(title: String, latitude: Double, longitude: Double, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val marker = PlanList2Item(title, latitude, longitude) // 마커 데이터 생성
        databaseReference.child(title).setValue(marker) // Firebase에 마커 저장
            .addOnSuccessListener { onSuccess() } // 성공 시 콜백 호출
            .addOnFailureListener { onFailure("마커 저장에 실패했습니다.") } // 실패 시 에러 메시지 전달
    }

    // 마커 삭제
    fun deleteMarker(title: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        databaseReference.child(title).removeValue() // Firebase에서 해당 마커 삭제
            .addOnSuccessListener { onSuccess() } // 성공 시 콜백 호출
            .addOnFailureListener { onFailure("마커 삭제에 실패했습니다.") } // 실패 시 에러 메시지 전달
    }

    // 저장된 마커 불러오기
    fun loadSavedPlaces(onDataLoaded: (List<PlanList2Item>) -> Unit, onError: (String) -> Unit) {
        databaseReference.addValueEventListener(object : ValueEventListener { // Firebase 데이터 변경 리스너 등록
            override fun onDataChange(snapshot: DataSnapshot) {
                val markers = mutableListOf<PlanList2Item>() // 마커 목록 생성
                for (dataSnapshot in snapshot.children) {
                    val planList2Item = dataSnapshot.getValue(PlanList2Item::class.java) // Firebase에서 마커 데이터 가져오기
                    planList2Item?.let { markers.add(it) } // null이 아닐 경우 목록에 추가
                }
                onDataLoaded(markers) // 마커 목록을 성공적으로 로드한 후 콜백 호출
            }

            override fun onCancelled(error: DatabaseError) {
                onError("마커 로드에 실패했습니다.") // 데이터 로드 실패 시 에러 메시지 전달
            }
        })
    }
}

