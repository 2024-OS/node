package com.example.node_project

import com.google.firebase.database.*

class PlanList2Repository {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("markers")

    // 마커 저장
    fun saveMarker(title: String, latitude: Double, longitude: Double, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val marker = PlanList2Item(title, latitude, longitude)
        databaseReference.child(title).setValue(marker)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure("마커 저장에 실패했습니다.") }
    }

    // 마커 삭제
    fun deleteMarker(title: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        databaseReference.child(title).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure("마커 삭제에 실패했습니다.") }
    }

    // 저장된 마커 불러오기
    fun loadSavedPlaces(onDataLoaded: (List<PlanList2Item>) -> Unit, onError: (String) -> Unit) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val markers = mutableListOf<PlanList2Item>()
                for (dataSnapshot in snapshot.children) {
                    val planList2Item = dataSnapshot.getValue(PlanList2Item::class.java)
                    planList2Item?.let { markers.add(it) }
                }
                onDataLoaded(markers)
            }

            override fun onCancelled(error: DatabaseError) {
                onError("마커 로드에 실패했습니다.")
            }
        })
    }
}