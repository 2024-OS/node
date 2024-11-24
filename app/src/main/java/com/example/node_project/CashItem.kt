package com.example.node_project

data class CashItem(
    val date: String = "",
    val amount: String = "",
    val content: String = "",
    val imageUrl: String = "" // Firebase에서 저장되는 이미지 URL
)

