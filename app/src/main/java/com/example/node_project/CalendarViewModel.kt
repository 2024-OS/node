package com.example.node_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.node_project.models.ScheduleItem
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewModel : ViewModel() {

    private val _selectedDate = MutableLiveData<String>().apply {
        value = getTodayDate() + " 할일"
    }
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _scheduleData = MutableLiveData<MutableMap<String, MutableList<ScheduleItem>>>().apply {
        value = mutableMapOf()
    }
    val scheduleData: LiveData<Map<String, List<ScheduleItem>>> = MutableLiveData<Map<String, List<ScheduleItem>>>().apply {
        value = _scheduleData.value?.mapValues { it.value.toList() } ?: mapOf()
    }

    private fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun setDate(date: String) {
        _selectedDate.value = "$date 할일"
    }

    fun addTask(task: String) {
        val date = _selectedDate.value ?: return
        val data = _scheduleData.value ?: mutableMapOf()
        val taskList = data.getOrPut(date) { mutableListOf() }
        taskList.add(ScheduleItem(task))
        _scheduleData.value = data
        updateScheduleData()
    }

    fun removeCheckedTasks() {
        val date = _selectedDate.value ?: return
        val data = _scheduleData.value ?: mutableMapOf()
        data[date]?.removeAll { it.isChecked }
        _scheduleData.value = data
        updateScheduleData()
    }

    // **체크 상태 업데이트**
    fun updateTaskCheckedState(position: Int, isChecked: Boolean) {
        val date = _selectedDate.value ?: return
        val data = _scheduleData.value ?: return
        val taskList = data[date] ?: return
        taskList[position].isChecked = isChecked
        _scheduleData.value = data // LiveData 갱신
    }

    fun getTasksForSelectedDate(): List<ScheduleItem> {
        val date = _selectedDate.value ?: return emptyList()
        return _scheduleData.value?.get(date) ?: emptyList()
    }

    private fun updateScheduleData() {
        (scheduleData as MutableLiveData).value = _scheduleData.value?.mapValues { it.value.toList() } ?: mapOf()
    }

    fun updateTaskText(position: Int, newText: String) {
        val date = _selectedDate.value ?: return
        val data = _scheduleData.value ?: return
        val taskList = data[date] ?: return

        // 특정 작업의 텍스트 업데이트
        taskList[position].task = newText
        _scheduleData.value = data // LiveData 갱신
    }


}
