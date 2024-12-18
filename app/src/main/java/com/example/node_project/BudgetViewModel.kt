package com.example.node_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.node_project.models.BudgetItem
import com.example.node_project.repository.BudgetRepository

class BudgetViewModel : ViewModel() {

    private val repository = BudgetRepository()

    private val _budgetItems = MutableLiveData<MutableList<BudgetItem>>(mutableListOf())
    val budgetItems: LiveData<MutableList<BudgetItem>> get() = _budgetItems

    init {
        fetchBudgetItems()
    }

    fun addBudgetItem(item: BudgetItem) {
        repository.addBudgetItem(item,
            onSuccess = { fetchBudgetItems() },
            onFailure = { println("오류 발생") }
        )
    }

    fun deleteCheckedItems() {
        val checkedItems = _budgetItems.value?.filter { it.isChecked } ?: emptyList()
        repository.deleteCheckedItems(checkedItems) {
            fetchBudgetItems()
        }
    }

    fun updateBudgetItem(itemId: String, updatedItem: BudgetItem) {
        repository.updateBudgetItem(itemId, updatedItem,
            onSuccess = { fetchBudgetItems() },
            onFailure = { println("오류 발생") }
        )
    }

    private fun fetchBudgetItems() {
        repository.fetchBudgetItems(
            onDataFetched = { items -> _budgetItems.postValue(items.toMutableList()) },
            onError = { error -> println("Error: $error") }
        )
    }
}
