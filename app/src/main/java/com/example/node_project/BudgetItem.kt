package com.example.node_project

data class BudgetItem(
    var id: String = "",
    var itemName: String = "",
    var itemQuantity: String = "1",
    var itemPrice: String = "100",
    var isChecked: Boolean = false
)
