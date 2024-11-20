package com.example.node_project

data class PlanItem(
    var title: String = "",             // 계획 제목
    var isChecked: Boolean = false,     // 체크박스 상태
    var score: Int = 0                   // 점수
)
