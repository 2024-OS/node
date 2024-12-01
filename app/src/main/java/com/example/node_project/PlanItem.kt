package com.example.node_project

data class PlanItem(
    var id: String? = null,
    var title: String = "새 장소",
    var isChecked: Boolean = false,
    var score: Int = 0
)
