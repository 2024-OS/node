package com.example.node_project

// 계획 아이템을 표현하는 데이터 클래스
data class PlanList1Item(
    var id: String? = null, // 아이템의 고유 ID
    var title: String = "새 장소", // 아이템 제목
    var isChecked: Boolean = false, // 체크박스 상태
    var score: Int = 0 // 아이템 점수
)
