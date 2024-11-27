package com.example.node_project.models

data class BudgetItem(
    var id: String = "",
    var itemName: String = "",
    var itemQuantity: String = "",
    var itemPrice: String = "",
    var itemDescription: String = "",
    var isChecked: Boolean = false // 기본값 false로 설정
)
