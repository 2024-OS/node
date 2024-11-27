package com.example.node_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.node_project.models.BudgetItem
import com.google.firebase.database.*

class BudgetViewModel : ViewModel() {

    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("budget")

    private val _budgetItems = MutableLiveData<MutableList<BudgetItem>>(mutableListOf())
    val budgetItems: LiveData<MutableList<BudgetItem>> get() = _budgetItems

    init {
        fetchBudgetItems()
    }

    fun addBudgetItem(item: BudgetItem) {
        val newItemRef = databaseRef.child("items").push()
        item.id = newItemRef.key ?: ""
        newItemRef.setValue(item)
            .addOnSuccessListener { println("Item added successfully") }
            .addOnFailureListener { println("Error adding item") }
    }

    fun deleteCheckedItems() {
        val currentList = _budgetItems.value ?: mutableListOf()
        for (item in currentList.filter { it.isChecked }) {
            if (item.id.isNotEmpty()) {
                databaseRef.child("items").child(item.id).removeValue()
            }
        }
        fetchBudgetItems()
    }

    fun updateBudgetItem(itemId: String, updatedItem: BudgetItem) {
        if (itemId.isNotEmpty()) {
            databaseRef.child("items").child(itemId).setValue(updatedItem)
                .addOnSuccessListener { println("Item updated successfully") }
                .addOnFailureListener { println("Error updating item") }
        }
    }

    private fun fetchBudgetItems() {
        databaseRef.child("items").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<BudgetItem>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(BudgetItem::class.java)
                    // 기본값 설정: null이면 false로 대체
                    if (item != null) {
                        item.isChecked = item.isChecked ?: false
                        items.add(item)
                    }
                }
                _budgetItems.postValue(items)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        })
    }
}
