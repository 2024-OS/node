package com.example.node_project

data class BudgetItem(
    val id: String = "",
    var itemName: String = "",
    var itemQuantity: String = "",
    var itemPrice: String = "",
    var itemLink: String = "", // 링크 속성 추가
    var isChecked: Boolean = false
)
