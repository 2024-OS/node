package com.example.node_project

data class PlanItem(
    var id: String = "",                // Firebase 고유 키
    var title: String = "새 장소",     // 계획 제목
    var isChecked: Boolean = false,     // 체크박스 상태
    var score: Int = 0                   // 점수
)