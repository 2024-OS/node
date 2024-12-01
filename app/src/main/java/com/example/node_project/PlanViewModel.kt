package com.example.node_project

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class PlanViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("plans")
    private val _plans = MutableLiveData<List<PlanItem>>()
    val plans: LiveData<List<PlanItem>> = _plans
    private val deletedItemIds = mutableSetOf<String>()

    init {
        loadPlans()
    }

    private fun loadPlans() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val planList = mutableListOf<PlanItem>()
                for (childSnapshot in snapshot.children) {
                    val plan = childSnapshot.getValue(PlanItem::class.java)
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

    fun addPlan(plan: PlanItem) {
        val newPlanRef = database.push()
        plan.id = newPlanRef.key
        newPlanRef.setValue(plan)
    }

    fun updatePlan(plan: PlanItem) {
        plan.id?.let { id ->
            database.child(id).setValue(plan)
        }
    }

    fun deletePlans(plans: List<PlanItem>) {
        val updates = HashMap<String, Any?>()
        plans.forEach { plan ->
            plan.id?.let { id ->
                updates[id] = null
                deletedItemIds.add(id)
            }
        }
        database.updateChildren(updates)
    }

    fun clearDeletedItems() {
        deletedItemIds.clear()
    }
}