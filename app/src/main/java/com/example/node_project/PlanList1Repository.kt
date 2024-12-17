package com.example.node_project

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*

class PlanList1Repository {
    // Firebase Realtime Database 참조
    private val database = FirebaseDatabase.getInstance().getReference("plans")

    // LiveData를 사용한 계획 목록 관리
    private val _plans = MutableLiveData<List<PlanList1Item>>()
    val plans: LiveData<List<PlanList1Item>> = _plans

    // 삭제된 아이템 ID 추적
    private val deletedItemIds = mutableSetOf<String>()

    init {
        loadPlans()
    }

    private fun loadPlans() {
        // Firebase에서 데이터 로드 및 실시간 업데이트
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val planList = mutableListOf<PlanList1Item>()
                for (childSnapshot in snapshot.children) {
                    val plan = childSnapshot.getValue(PlanList1Item::class.java)
                    plan?.let {
                        it.id = childSnapshot.key
                        if (it.id !in deletedItemIds) {
                            planList.add(it)
                        }
                    }
                }
                _plans.value = planList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("YourTag", "Database operation cancelled: ${error.toException()}")
            }
        })
    }

    // 새 계획 추가
    fun addPlan(plan: PlanList1Item) {
        val newPlanRef = database.push()
        plan.id = newPlanRef.key
        newPlanRef.setValue(plan)
    }

    // 계획 업데이트
    fun updatePlan(plan: PlanList1Item) {
        plan.id?.let { id ->
            database.child(id).setValue(plan)
        }
    }

    // 선택된 계획들 삭제
    fun deletePlans(plans: List<PlanList1Item>) {
        val updates = HashMap<String, Any?>()
        plans.forEach { plan ->
            plan.id?.let { id ->
                updates[id] = null
                deletedItemIds.add(id)
            }
        }
        database.updateChildren(updates)
    }

    // 삭제된 아이템 목록 초기화
    fun clearDeletedItems() {
        deletedItemIds.clear()
    }
}
