package com.example.node_project.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.node_project.models.ScheduleItem
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


 //캘린더와 관련된 데이터 및 작업(Task)을 관리하는 ViewModel 클래스.

class CalendarViewModel : ViewModel() {

    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("tasks")
    private val _selectedDate = MutableLiveData<String>()    // 현재 선택된 날짜를 저장
    val selectedDate: LiveData<String> get() = _selectedDate
    private val _allTasks = MutableLiveData<MutableMap<String, MutableList<ScheduleItem>>>()
    private val _tasksForDate = MutableLiveData<List<ScheduleItem>>()    // 현재 선택된 날짜에 해당하는 작업만 저장
    val tasksForDate: LiveData<List<ScheduleItem>> get() = _tasksForDate


    fun initializeDate() {
        // 현재 날짜를 가져와 저장
        val today = getCurrentDate()
        _selectedDate.value = today // 현재 날짜를 선택된 날짜로 설정
        _tasksForDate.value = _allTasks.value?.get(today) ?: emptyList() // 현재 날짜에 해당하는 작업 리스트를 설정
    }

    //현재 날짜를 "yyyy년 MM월 dd일" 형식으로 반환
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance() // 현재 시간을 가져오는 객체
        val format = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()) // 날짜를 나타내는 형식 지정
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul") // 한국 표준시로 설정
        return format.format(calendar.time) // 날짜 반환
    }

    //사용자가 특정 날짜를 선택했을 때 호출되는 함수.
    fun setDate(date: String) {
        _selectedDate.value = date // 선택된 날짜를 업데이트
        _tasksForDate.value = _allTasks.value?.get(date) ?: emptyList() // 해당 날짜의 작업 리스트를 업데이트
    }

    //Firebase 데이터베이스에서 데이터를 가져옴 v
    fun fetchTasks() {
        // Firebase에서 데이터를 읽기 위해 ValueEventListener 추가
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 모든 작업 데이터를 저장할 MutableMap 생성
                val allTasks = mutableMapOf<String, MutableList<ScheduleItem>>()

                // Firebase 데이터 구조를 탐색하며 작업 데이터를 읽음
                for (dateSnapshot in snapshot.children) {
                    val date = dateSnapshot.key ?: continue // 날짜 키를 읽고 null이면 건너뜀
                    val taskList = dateSnapshot.children.mapNotNull {
                        it.getValue(ScheduleItem::class.java) // Firebase 데이터를 ScheduleItem 객체로 변환
                    }.toMutableList()
                    allTasks[date] = taskList // 날짜별 작업 리스트 저장
                }

                // 데이터를 LiveData에 저장
                _allTasks.postValue(allTasks)

                // 현재 선택된 날짜가 있다면 해당 날짜의 데이터 업데이트
                _selectedDate.value?.let {
                    _tasksForDate.postValue(allTasks[it] ?: emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Firebase 데이터 읽기 실패 시 오류 출력
                println("Firebase 데이터 로드 에러: ${error.message}")
            }
        })
    }

    // 선택된 날짜에 리스트 추가
    fun addTask(task: String) {
        val date = _selectedDate.value ?: return // 선택된 날짜가 없으면 함수 종료
        val allTasks = _allTasks.value ?: mutableMapOf() // 모든 작업 데이터를 가져옴
        val taskList = allTasks.getOrPut(date) { mutableListOf() } // 선택된 날짜의 작업 리스트 가져오기 (없으면 새로 생성)
        taskList.add(ScheduleItem(task)) // 새 작업 추가
        _allTasks.value = allTasks // 모든 작업 데이터 업데이트
        _tasksForDate.postValue(taskList) // UI에 업데이트된 작업 데이터 반영

        // Firebase 데이터베이스에 저장
        saveTasksToFirebase(date, taskList)
    }

    //선택된 날짜의 체크된 작업(완료된 작업) 삭제
    fun removeCheckedTasks() {
        val date = _selectedDate.value ?: return // 선택된 날짜가 없으면 함수 종료
        val allTasks = _allTasks.value ?: mutableMapOf() // 데이터를 가져옴
        val updatedTaskList = allTasks[date]?.filterNot { it.isChecked }?.toMutableList() ?: mutableListOf() // 체크되지 않은 작업만 남김
        allTasks[date] = updatedTaskList // 선택된 날짜의 작업 리스트를 업데이트
        _allTasks.value = allTasks // 모든 작업 데이터 업데이트
        _tasksForDate.postValue(updatedTaskList) // UI에 업데이트된 작업 데이터 반영

        // Firebase 데이터베이스에 저장
        saveTasksToFirebase(date, updatedTaskList)
    }

    //특정 작업의 체크 상태를 업데이트
    fun updateTaskCheckedState(position: Int, isChecked: Boolean) {
        val date = _selectedDate.value ?: return // 선택된 날짜가 없으면 함수 종료
        val allTasks = _allTasks.value ?: return // 데이터를 가져옴
        val taskList = allTasks[date] ?: return // 선택된 날짜의 리스트 가져오기

        // 작업 리스트 내 해당 작업의 체크 상태를 업데이트
        if (position in taskList.indices) {
            taskList[position].isChecked = isChecked // 체크 상태 변경
            _allTasks.value = allTasks // 모든 작업 데이터 업데이트
            _tasksForDate.postValue(taskList) // UI에 데이터 반영

            // Firebase 데이터베이스에 저장
            saveTasksToFirebase(date, taskList)
        }
    }

    //텍스트를 수정
    fun updateTaskText(position: Int, newText: String) {
        val date = _selectedDate.value ?: return // 선택된 날짜가 없으면 함수 종료
        val allTasks = _allTasks.value ?: return // 모든 작업 데이터를 가져옴
        val taskList = allTasks[date] ?: return // 선택된 날짜의 작업 리스트 가져오기

        // 작업 리스트 내 해당 작업의 텍스트를 업데이트
        if (position in taskList.indices) {
            taskList[position].task = newText // 작업 내용 업데이트
            _allTasks.value = allTasks // 모든 작업 데이터 업데이트
            _tasksForDate.postValue(taskList) // UI에 데이터 반영

            // Firebase 데이터베이스에 저장
            saveTasksToFirebase(date, taskList)
        }
    }

    //Firebase 데이터베이스에 데이터를 저장
    private fun saveTasksToFirebase(date: String, taskList: List<ScheduleItem>) {
        databaseRef.child(date).setValue(taskList) // Firebase 데이터베이스에 작업 저장
            .addOnSuccessListener {
                println("작업 저장 성공: $date") //
            }
            .addOnFailureListener { error ->
                println("작업 저장 실패: ${error.message}")
            }
    }
}
