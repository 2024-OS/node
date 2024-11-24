package com.example.node_project

data class CashItem(
    var date: String = "",        // 날짜
    var amount: String = "",      // 금액
    var content: String = "",     // 내용
    var imageUri: String? = null  // 이미지 URI (nullable)
)

