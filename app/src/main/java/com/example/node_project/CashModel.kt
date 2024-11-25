// 데이터를 나타내는 Model 부분
package com.example.node_project

data class CashModel(
    val date: String = "",
    val amount: String = "",
    val content: String = "",
    val imageUrl: String = "" // Firebase에서 저장되는 이미지 URL
)