package com.example.node_project

// 마커의 데이터 구조를 정의하는 데이터 클래스
data class PlanList2Item(
    val title: String = "", // 마커 제목
    val latitude: Double = 0.0, // 위도
    val longitude: Double = 0.0 // 경도
)
