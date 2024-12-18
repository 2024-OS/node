package com.example.node_project.repository

import com.example.node_project.models.BudgetItem
import com.google.firebase.database.*

class BudgetRepository {

    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("budget")

    fun addBudgetItem(item: BudgetItem, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val newItemRef = databaseRef.child("items").push()
        item.id = newItemRef.key ?: ""
        newItemRef.setValue(item)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    fun deleteCheckedItems(checkedItems: List<BudgetItem>, onComplete: () -> Unit) {
        checkedItems.forEach { item ->
            if (item.id.isNotEmpty()) {
                databaseRef.child("items").child(item.id).removeValue()
            }
        }
        onComplete()
    }

    fun updateBudgetItem(itemId: String, updatedItem: BudgetItem, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (itemId.isNotEmpty()) {
            databaseRef.child("items").child(itemId).setValue(updatedItem)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure() }
        }
    }

    fun fetchBudgetItems(onDataFetched: (List<BudgetItem>) -> Unit, onError: (String) -> Unit) {
        databaseRef.child("items").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<BudgetItem>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(BudgetItem::class.java)
                    if (item != null) {
                        items.add(item)
                    }
                }
                onDataFetched(items)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })
    }
}
