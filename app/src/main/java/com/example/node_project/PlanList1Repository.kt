package com.example.node_project

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*

// 계획 목록을 Firebase에서 관리하는 레포지토리 클래스
class PlanList1Repository {
    private val database = FirebaseDatabase.getInstance().getReference("plans") // "plans" 경로에 접근

    // LiveData를 사용한 계획 목록 관리
    private val _plans = MutableLiveData<List<PlanList1Item>>() // 내부 계획 목록
    val plans: LiveData<List<PlanList1Item>> = _plans // 외부에서 접근 가능한 계획 목록

    // 삭제된 아이템 ID 추적
    private val deletedItemIds = mutableSetOf<String>() // 삭제된 아이템 ID 목록

    init {
        loadPlans() // 초기화 시 계획 목록 로드
    }

    private fun loadPlans() {
        // Firebase에서 데이터 로드 및 실시간 업데이트
        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val planList = mutableListOf<PlanList1Item>() // 새로운 계획 목록
                for (childSnapshot in snapshot.children) {
                    val plan = childSnapshot.getValue(PlanList1Item::class.java) // Firebase에서 아이템 가져오기
                    plan?.let {
                        it.id = childSnapshot.key // 아이템의 ID 설정
                        if (it.id !in deletedItemIds) { // 삭제된 아이템이 아닐 경우
                            planList.add(it) // 목록에 추가
                        }
                    }
                }
                _plans.value = planList // LiveData에 새로운 계획 목록 설정
                // 계획 목록이 변경될 때마다 UI가 자동으로 업데이트됨
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("", "Database operation cancelled: ${error.toException()}") // 오류 로그
                // 데이터베이스 작업이 취소된 경우에 대한 처리
            }
        })
    }

    // 새 계획 추가
    fun addPlan(plan: PlanList1Item) {
        val newPlanRef = database.push() // Firebase에 새로운 아이템 추가를 위한 레퍼런스 생성
        plan.id = newPlanRef.key // 생성된 ID를 아이템에 설정
        newPlanRef.setValue(plan) // Firebase에 아이템 저장
        // ViewModel -> PlanList1Repository (addPlan 메서드 호출)
        // 새로운 데이터가 추가되면 UI가 자동으로 업데이트됨
    }

    // 계획 업데이트
    fun updatePlan(plan: PlanList1Item) {
        plan.id?.let { id -> // 아이템의 ID가 존재할 경우
            database.child(id).setValue(plan) // Firebase에서 해당 아이템 업데이트
            // ViewModel -> PlanList1Repository (updatePlan 메서드 호출)
            // 데이터가 업데이트되면 UI가 자동으로 반영됨
        }
    }

    // 선택된 계획들 삭제
    fun deletePlans(plans: List<PlanList1Item>) {
        val updates = HashMap<String, Any?>() // Firebase에 전송할 업데이트 맵 생성
        plans.forEach { plan ->
            plan.id?.let { id -> // 각 계획의 ID가 존재할 경우
                updates[id] = null // 해당 ID를 null로 설정하여 삭제할 준비
                deletedItemIds.add(id) // 삭제된 아이템 ID 목록에 추가
            }
        }
        database.updateChildren(updates) // Firebase에 업데이트 전송
        // ViewModel -> PlanList1Repository (deletePlans 메서드 호출)
        // 삭제된 데이터는 LiveData에서 자동으로 반영됨
    }

    // 삭제된 아이템 목록 초기화
    fun clearDeletedItems() {
        deletedItemIds.clear() // 삭제된 아이템 ID 목록을 초기화
        // Fragment가 종료될 때 삭제된 항목을 정리하여 메모리 누수 방지
    }
}
