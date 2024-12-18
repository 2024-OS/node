package com.example.node_project.repository

import com.example.node_project.models.BudgetItem
import com.google.firebase.database.*

class BudgetRepository {

    // Firebase 데이터베이스의 'budget/items' 경로를 참조
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("budget")

    //새 예산 항목 추가
    fun addBudgetItem(item: BudgetItem, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val newItemRef = databaseRef.child("items").push() // 고유 키 생성
        item.id = newItemRef.key ?: "" // 생성된 키를 BudgetItem ID로 설정
        newItemRef.setValue(item) // 항목 데이터를 Firebase에 저장
            .addOnSuccessListener { onSuccess() } // 저장 성공 시 콜백 호출
            .addOnFailureListener { onFailure() } // 저장 실패 시 콜백 호출
    }

    //체크된 항목 삭제
    fun deleteCheckedItems(checkedItems: List<BudgetItem>, onComplete: () -> Unit) {
        checkedItems.forEach { item ->
            if (item.id.isNotEmpty()) { // 유효한 ID가 있는 경우에만 삭제
                databaseRef.child("items").child(item.id).removeValue() // Firebase에서 항목 삭제
            }
        }
        onComplete() // 삭제 작업 완료 후 콜백 호출
    }

    //예산 항목 업데이트
    fun updateBudgetItem(itemId: String, updatedItem: BudgetItem, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (itemId.isNotEmpty()) { // 유효한 ID인 경우에만 업데이트
            databaseRef.child("items").child(itemId).setValue(updatedItem) // Firebase에 업데이트된 데이터 저장
                .addOnSuccessListener { onSuccess() } // 업데이트 성공 시 콜백 호출
                .addOnFailureListener { onFailure() } // 업데이트 실패 시 콜백 호출
        }
    }

    //Firebase에서 예산 항목 데이터 가져오기
    fun fetchBudgetItems(onDataFetched: (List<BudgetItem>) -> Unit, onError: (String) -> Unit) {
        databaseRef.child("items").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<BudgetItem>() // 데이터를 저장할 리스트
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(BudgetItem::class.java) // 데이터를 BudgetItem 객체로 변환
                    if (item != null) {
                        items.add(item) // 변환 성공 시 리스트에 추가
                    }
                }
                onDataFetched(items) // 데이터 가져오기 성공 시 콜백 호출
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message) // 데이터 가져오기 실패 시 에러 메시지를 콜백으로 전달
            }
        })
    }
}
