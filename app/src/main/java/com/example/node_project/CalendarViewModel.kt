package com.example.node_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.node_project.models.ScheduleItem
import com.example.node_project.repository.CalendarTaskRepository
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewModel : ViewModel() {

    private val repository = CalendarTaskRepository()
    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _tasksForDate = MutableLiveData<List<ScheduleItem>>()
    val tasksForDate: LiveData<List<ScheduleItem>> get() = _tasksForDate

    private val allTasks = mutableMapOf<String, MutableList<ScheduleItem>>()

    fun initializeDate() {
        val today = getCurrentDate()
        _selectedDate.value = today
        loadTasksForDate(today)
    }

    private fun getCurrentDate(): String {
        val format = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        return format.format(Calendar.getInstance().time)
    }

    fun setDate(date: String) {
        _selectedDate.value = date
        loadTasksForDate(date)
    }

    fun fetchTasks() {
        repository.fetchTasks { tasks ->
            allTasks.clear()
            allTasks.putAll(tasks.mapValues { it.value.toMutableList() })
            _selectedDate.value?.let { loadTasksForDate(it) }
        }
    }

    fun addTask(task: String) {
        val date = _selectedDate.value ?: return
        val taskList = allTasks.getOrPut(date) { mutableListOf() }
        taskList.add(ScheduleItem(task))

        allTasks[date] = taskList
        _tasksForDate.postValue(taskList) // postValue 사용
        repository.saveTasks(date, taskList) {}
    }

    fun removeCheckedTasks() {
        val date = _selectedDate.value ?: return
        val updatedTasks = allTasks[date]?.filterNot { it.isChecked }?.toMutableList() ?: return

        allTasks[date] = updatedTasks
        _tasksForDate.postValue(updatedTasks) // postValue 사용
        repository.saveTasks(date, updatedTasks) {}
    }

    fun updateTaskCheckedState(position: Int, isChecked: Boolean) {
        val date = _selectedDate.value ?: return
        val taskList = allTasks[date]?.toMutableList() ?: return

        if (position in taskList.indices) {
            taskList[position].isChecked = isChecked
            allTasks[date] = taskList
            _tasksForDate.postValue(taskList) // postValue 사용
            repository.saveTasks(date, taskList) {}
        }
    }

    fun updateTaskText(position: Int, newText: String) {
        val date = _selectedDate.value ?: return
        val taskList = allTasks[date]?.toMutableList() ?: return

        if (position in taskList.indices) {
            taskList[position].task = newText
            allTasks[date] = taskList
            _tasksForDate.postValue(taskList) // postValue 사용
            repository.saveTasks(date, taskList) {}
        }
    }

    private fun loadTasksForDate(date: String) {
        _tasksForDate.postValue(allTasks[date] ?: emptyList())
    }
}
