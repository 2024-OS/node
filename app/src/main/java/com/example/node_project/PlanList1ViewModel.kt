package com.example.node_project

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PlanList1ViewModel : ViewModel() {
    private val repository = PlanList1Repository()

    val plans: LiveData<List<PlanList1Item>> = repository.plans

    // 새 계획 추가
    fun addPlan(plan: PlanList1Item) {
        repository.addPlan(plan)
    }

    // 계획 업데이트
    fun updatePlan(plan: PlanList1Item) {
        repository.updatePlan(plan)
    }

    // 선택된 계획들 삭제
    fun deletePlans(plans: List<PlanList1Item>) {
        repository.deletePlans(plans)
    }

    // 삭제된 아이템 목록 초기화
    fun clearDeletedItems() {
        repository.clearDeletedItems()
    }
}
