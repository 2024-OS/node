package com.example.node_project.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.node_project.models.ScheduleItem
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewModel : ViewModel() {

    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("tasks")

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _allTasks = MutableLiveData<MutableMap<String, MutableList<ScheduleItem>>>()
    private val _tasksForDate = MutableLiveData<List<ScheduleItem>>()
    val tasksForDate: LiveData<List<ScheduleItem>> get() = _tasksForDate

    // 현재 날짜를 가져오는 함수
    fun initializeDate() {
        val today = getCurrentDate()  // 현재 날짜를 가져오는 함수 호출
        _selectedDate.value = today
        _tasksForDate.value = _allTasks.value?.get(today) ?: emptyList()
    }

    // 현재 날짜를 "yyyy년 MM월 dd일" 형식으로 반환하는 함수
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul") // KST로 타임존 설정
        return format.format(calendar.time)
    }

    fun setDate(date: String) {
        _selectedDate.value = date
        _tasksForDate.value = _allTasks.value?.get(date) ?: emptyList()
    }

    fun fetchTasks() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allTasks = mutableMapOf<String, MutableList<ScheduleItem>>()
                for (dateSnapshot in snapshot.children) {
                    val date = dateSnapshot.key ?: continue
                    val taskList = dateSnapshot.children.mapNotNull {
                        it.getValue(ScheduleItem::class.java)
                    }.toMutableList()
                    allTasks[date] = taskList
                }
                _allTasks.postValue(allTasks)

                _selectedDate.value?.let {
                    _tasksForDate.postValue(allTasks[it] ?: emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        })
    }

    fun addTask(task: String) {
        val date = _selectedDate.value ?: return
        val allTasks = _allTasks.value ?: mutableMapOf()
        val taskList = allTasks.getOrPut(date) { mutableListOf() }
        taskList.add(ScheduleItem(task))
        _allTasks.value = allTasks
        _tasksForDate.postValue(taskList)

        saveTasksToFirebase(date, taskList)
    }

    fun removeCheckedTasks() {
        val date = _selectedDate.value ?: return
        val allTasks = _allTasks.value ?: mutableMapOf()
        val updatedTaskList = allTasks[date]?.filterNot { it.isChecked }?.toMutableList() ?: mutableListOf()
        allTasks[date] = updatedTaskList
        _allTasks.value = allTasks
        _tasksForDate.postValue(updatedTaskList)

        saveTasksToFirebase(date, updatedTaskList)
    }

    fun updateTaskCheckedState(position: Int, isChecked: Boolean) {
        val date = _selectedDate.value ?: return
        val allTasks = _allTasks.value ?: return
        val taskList = allTasks[date] ?: return

        if (position in taskList.indices) {
            taskList[position].isChecked = isChecked
            _allTasks.value = allTasks
            _tasksForDate.postValue(taskList)

            saveTasksToFirebase(date, taskList)
        }
    }

    fun updateTaskText(position: Int, newText: String) {
        val date = _selectedDate.value ?: return
        val allTasks = _allTasks.value ?: return
        val taskList = allTasks[date] ?: return

        if (position in taskList.indices) {
            taskList[position].task = newText
            _allTasks.value = allTasks
            _tasksForDate.postValue(taskList)

            saveTasksToFirebase(date, taskList)
        }
    }

    private fun saveTasksToFirebase(date: String, taskList: List<ScheduleItem>) {
        databaseRef.child(date).setValue(taskList)
            .addOnSuccessListener {
                println("Tasks successfully saved for date: $date")
            }
            .addOnFailureListener { error ->
                println("Error saving tasks: ${error.message}")
            }
    }
}
