package com.example.node_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.node_project.models.BudgetItem
import com.google.firebase.database.*


//예산 관리(ViewModel) 클래스 (예산 데이터를 가져오고 업데이트)
class BudgetViewModel : ViewModel() {

    // Firebase 데이터베이스의 "budget" 경로를 참조
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("budget")

    // MutableLiveData: 예산 항목 리스트를 내부에서 관리
    private val _budgetItems = MutableLiveData<MutableList<BudgetItem>>(mutableListOf())

    // LiveData: 예산 항목 리스트를 외부에 읽기 전용으로 보여줌
    val budgetItems: LiveData<MutableList<BudgetItem>> get() = _budgetItems

    //ViewModel 초기화 시 Firebase에서 데이터를 가져옴
    init {
        fetchBudgetItems() // 예산 항목 리스트를 가져옴
    }

    //새로운 예산 항목 추가
    fun addBudgetItem(item: BudgetItem) {
        val newItemRef = databaseRef.child("items").push() // 고유 키를 생성하여 새 항목을 추가
        item.id = newItemRef.key ?: "" // 항목의 ID를 생성된 키로 설정
        newItemRef.setValue(item) // Firebase에 항목 저장
            .addOnSuccessListener { println("성공") }
            .addOnFailureListener { println("실패") }
    }

    //체크된 예산 항목 삭제
    fun deleteCheckedItems() {
        val currentList = _budgetItems.value ?: mutableListOf() // 예산 항목 리스트 불러옴
        for (item in currentList.filter { it.isChecked }) { // 체크된 항목만 선택
            if (item.id.isNotEmpty()) { // 항목 ID가 존재하는 경우
                databaseRef.child("items").child(item.id).removeValue() // Firebase에서 해당 항목 삭제
            }
        }
        fetchBudgetItems() // 삭제 후 최신 데이터 불러옴
    }

    //예산 항목 업데이트
    fun updateBudgetItem(itemId: String, updatedItem: BudgetItem) {
        if (itemId.isNotEmpty()) { // 유효한 ID인 경우
            databaseRef.child("items").child(itemId).setValue(updatedItem) // Firebase에서 항목 업데이트
                .addOnSuccessListener { println("Item updated successfully") } // 성공 시 메시지 출력
                .addOnFailureListener { println("Error updating item") } // 실패 시 메시지 출력
        }
    }

    //Firebase에서 예산 항목 데이터를 불러옴
    private fun fetchBudgetItems() {
        databaseRef.child("items").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<BudgetItem>() // 가져온 데이터를 저장할 리스트 생성
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(BudgetItem::class.java) // 데이터를 BudgetItem 객체로 변환
                    if (item != null) {
                        item.isChecked = item.isChecked ?: false // isChecked가 null이면 false로 설정
                        items.add(item) // 리스트에 항목 추가
                    }
                }
                _budgetItems.postValue(items) // MutableLiveData에 데이터 업데이트
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        })
    }
}
