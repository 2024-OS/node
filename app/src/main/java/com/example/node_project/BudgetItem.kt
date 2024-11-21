package com.example.node_project

data class BudgetItem(
    var itemName: String = "",
    var itemQuantity: String = "",
    var itemPrice: String = "",
    var itemDescription: String = "", // 설명 필드 추가
    var isChecked: Boolean = false
)
