package com.example.node_project.repository

import com.example.node_project.models.CalendarScheduleItem
import com.google.firebase.database.*


class CalendarTaskRepository {

    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("tasks")
    // Firebase Realtime Database의 "tasks" 경로를 참조

    // 모든 작업 데이터를 가져옴
    fun fetchTasks(callback: (Map<String, List<CalendarScheduleItem>>) -> Unit) {
        // Firebase에서 데이터 읽어옴
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Firebase에서 가져온 데이터를 저장할 Map
                val tasks = mutableMapOf<String, List<CalendarScheduleItem>>()

                // 각 날짜의 작업 데이터를 읽어 Map에 저장
                for (dateSnapshot in snapshot.children) {
                    val taskList = dateSnapshot.children.mapNotNull {
                        it.getValue(CalendarScheduleItem::class.java) // CalendarScheduleItem 객체로 변환
                    }
                    tasks[dateSnapshot.key!!] = taskList // 날짜를 키로, 작업 리스트를 값으로 저장
                }

                callback(tasks) // 데이터를 콜백 함수에 전달
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // 특정 날짜의 작업 데이터를 저장하
    fun saveTasks(date: String, taskList: List<CalendarScheduleItem>, callback: () -> Unit) {
        // Firebase Database에 데이터 저장
        databaseRef.child(date).setValue(taskList).addOnSuccessListener {
            callback() // 저장이 완료되면 콜백 함수 호출
        }
    }
}
