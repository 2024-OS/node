package com.example.node_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.node_project.models.CalendarScheduleItem
import com.example.node_project.repository.CalendarTaskRepository
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewModel : ViewModel() {

    private val repository = CalendarTaskRepository()
    private val _selectedDate = MutableLiveData<String>() // 선택된 날짜
    val selectedDate: LiveData<String> get() = _selectedDate // 외부에서 관찰 가능한 LiveData

    private val _tasksForDate = MutableLiveData<List<CalendarScheduleItem>>() // 선택된 날짜의 리스트
    val tasksForDate: LiveData<List<CalendarScheduleItem>> get() = _tasksForDate // 외부에서 관찰 가능한 LiveData

    private val allTasks = mutableMapOf<String, MutableList<CalendarScheduleItem>>() // 모든 날짜의 작업 데이터를 저장

    // 초기 날짜를 설정
    fun initializeDate() {
        val today = getCurrentDate() // 현재 날짜
        _selectedDate.value = today // LiveData에 설정
        loadTasksForDate(today) // 해당 날짜의 작업 데이터를 로드
    }

    // 현재 날짜를 지정된 형식의 문자열로 반환
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")) // 한국 시간대 설정
        val format = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(calendar.time)
    }

    // 사용자가 특정 날짜를 선택 시
    fun setDate(date: String) {
        _selectedDate.value = date // LiveData에 설정
        loadTasksForDate(date) // 해당 날짜의 작업 데이터를 로드
    }

    // Firebase에서 데이터를 불러옴
    fun fetchTasks() {
        repository.fetchTasks { tasks ->
            allTasks.clear() // 기존 데이터를 초기화
            allTasks.putAll(tasks.mapValues { it.value.toMutableList() }) // Firebase 데이터 로드
            _selectedDate.value?.let { loadTasksForDate(it) } // 현재 선택된 날짜의 작업 데이터를 로드
        }
    }

    // 새로운 할 일을 추가
    fun addTask(task: String) {
        val date = _selectedDate.value ?: return // 선택된 날짜가 없으면 중단
        val taskList = allTasks.getOrPut(date) { mutableListOf() } // 날짜에 해당하는 작업 리스트 가져오기
        taskList.add(CalendarScheduleItem(task)) // 새 할 일 추가

        allTasks[date] = taskList // 작업 리스트 업데이트
        _tasksForDate.postValue(taskList) // LiveData 업데이트
        repository.saveTasks(date, taskList) {} // Firebase에 저장
    }

    // 체크된 할 일을 삭제(덮어쓰기)
    fun removeCheckedTasks() {
        val date = _selectedDate.value ?: return // 선택된 날짜가 없으면 중단
        val updatedTasks = allTasks[date]?.filterNot { it.isChecked }?.toMutableList() ?: return // 체크되지 않은 작업만 필터링

        allTasks[date] = updatedTasks // 업데이트된 작업 리스트 저장
        _tasksForDate.postValue(updatedTasks) // LiveData 업데이트
        repository.saveTasks(date, updatedTasks) {} // Firebase에 저장
    }

    // 할 일의 체크 상태를 업데이트
    fun updateTaskCheckedState(position: Int, isChecked: Boolean) {
        val date = _selectedDate.value ?: return // 선택된 날짜가 없으면 중단
        val taskList = allTasks[date]?.toMutableList() ?: return // 작업 리스트 가져오기

        if (position in taskList.indices) {
            taskList[position].isChecked = isChecked // 체크 상태 업데이트
            allTasks[date] = taskList // 할 일 리스트 저장
            _tasksForDate.postValue(taskList) // LiveData 업데이트
            repository.saveTasks(date, taskList) {} // Firebase에 저장
        }
    }

    // 할 일의 텍스트를 업데이트
    fun updateTaskText(position: Int, newText: String) {
        val date = _selectedDate.value ?: return // 선택된 날짜가 없으면 중단
        val taskList = allTasks[date]?.toMutableList() ?: return // 작업 리스트 가져오기

        if (position in taskList.indices) {
            taskList[position].task = newText // 작업 텍스트 업데이트
            allTasks[date] = taskList // 작업 리스트 저장
            _tasksForDate.postValue(taskList) // LiveData 업데이트
            repository.saveTasks(date, taskList) {} // Firebase에 저장
        }
    }

    // 특정 날짜의 작업 데이터를 불러옴
    private fun loadTasksForDate(date: String) {
        _tasksForDate.postValue(allTasks[date] ?: emptyList()) // 작업 데이터가 없으면 빈 리스트 반환
    }
}
