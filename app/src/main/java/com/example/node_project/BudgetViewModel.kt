package com.example.node_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.node_project.models.BudgetItem
import com.example.node_project.repository.BudgetRepository


class BudgetViewModel : ViewModel() {

    private val repository = BudgetRepository() // Firebase 작업을 처리하는 Repository 객체

    // 예산 항목 목록을 저장하는 LiveData 객체
    private val _budgetItems = MutableLiveData<MutableList<BudgetItem>>(mutableListOf())
    val budgetItems: LiveData<MutableList<BudgetItem>> get() = _budgetItems // 외부에서 읽기 전용

    init {
        fetchBudgetItems() // ViewModel 초기화 시 Firebase 데이터 가져오기
    }

    //새 예산 항목을 추가
    fun addBudgetItem(item: BudgetItem) {
        repository.addBudgetItem(
            item,
            onSuccess = { fetchBudgetItems() }, // 성공 시 데이터를 다시 가져와 업데이트
            onFailure = { println("오류 발생") }
        )
    }

    // 체크된 예산 항목을 삭제
    fun deleteCheckedItems() {
        val checkedItems = _budgetItems.value?.filter { it.isChecked } ?: emptyList() // 체크된 항목만 필터링
        repository.deleteCheckedItems(checkedItems) {
            fetchBudgetItems() // 삭제 완료 후 데이터를 다시 가져와 업데이트
        }
    }

    // 예산 항목을 업데이트(수정)합니다.
    fun updateBudgetItem(itemId: String, updatedItem: BudgetItem) {
        repository.updateBudgetItem(
            itemId,
            updatedItem,
            onSuccess = { fetchBudgetItems() }, // 성공 시 데이터를 다시 가져와 업데이트
            onFailure = { println("오류 발생") }
        )
    }

    // Firebase에서 예산 항목 데이터를 불러옴
    private fun fetchBudgetItems() {
        repository.fetchBudgetItems(
            onDataFetched = { items -> _budgetItems.postValue(items.toMutableList()) }, // 성공 시 LiveData 업데이트
            onError = { error -> println("Error: $error") }
        )
    }
}
