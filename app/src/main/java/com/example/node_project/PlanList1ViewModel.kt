package com.example.node_project

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

// ViewModel 클래스: UI 관련 데이터를 관리
// Fragment와 Repository 간의 중재 역할을 수행
class PlanList1ViewModel : ViewModel() {
    private val repository = PlanList1Repository() // Repository 인스턴스 생성

    val plans: LiveData<List<PlanList1Item>> = repository.plans // Repository에서 계획 목록 가져오기

    // 새 계획 추가
    fun addPlan(plan: PlanList1Item) {
        repository.addPlan(plan) // Repository에 새 계획 추가 요청
        // UI와 데이터의 동기화를 위해 Repository에 요청
    }

    // 계획 업데이트
    fun updatePlan(plan: PlanList1Item) {
        repository.updatePlan(plan) // Repository에 계획 업데이트 요청
        // ViewModel을 통해 UI에 반영
    }

    // 선택된 계획들 삭제
    fun deletePlans(plans: List<PlanList1Item>) {
        repository.deletePlans(plans) // Repository에 삭제 요청
        // 삭제된 데이터는 자동으로 UI에서 업데이트됨
    }

    // 삭제된 아이템 목록 초기화
    fun clearDeletedItems() {
        repository.clearDeletedItems() // Repository의 삭제된 아이템 목록 초기화 요청
        // Fragment가 종료될 때 메모리 관리에 도움
    }
}
