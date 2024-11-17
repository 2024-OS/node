package com.example.node_project

data class PlanItem(
    val title: String,               // 계획 제목
    val description: String?,        // 계획 설명 (선택적)
    var isChecked: Boolean = false   // 체크박스 상태
)
