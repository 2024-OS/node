package com.example.node_project.repository

import com.example.node_project.models.ScheduleItem
import com.google.firebase.database.*

class CalendarTaskRepository {

    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("tasks")

    // 모든 작업 데이터를 가져옴
    fun fetchTasks(callback: (Map<String, List<ScheduleItem>>) -> Unit) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = mutableMapOf<String, List<ScheduleItem>>()
                for (dateSnapshot in snapshot.children) {
                    val taskList = dateSnapshot.children.mapNotNull {
                        it.getValue(ScheduleItem::class.java)
                    }
                    tasks[dateSnapshot.key!!] = taskList
                }
                callback(tasks)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 특정 날짜의 데이터를 저장
    fun saveTasks(date: String, taskList: List<ScheduleItem>, callback: () -> Unit) {
        databaseRef.child(date).setValue(taskList).addOnSuccessListener {
            callback()
        }
    }
}
