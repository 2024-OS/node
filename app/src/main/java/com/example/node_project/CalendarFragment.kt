package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.node_project.databinding.FragmentCalenderBinding
import com.example.node_project.viewmodel.CalendarViewModel

// 캘린더 화면을 구성하는 Fragment 클래스
class CalendarFragment : Fragment() {

    private val viewModel: CalendarViewModel by viewModels()
    private var _binding: FragmentCalenderBinding? = null // View Binding 객체
    private val binding get() = _binding!! // null 안전성을

    private lateinit var adapter: ScheduleAdapter // RecyclerView를 위한 어댑터 변수


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalenderBinding.inflate(inflater, container, false) // View Binding 초기화
        binding.viewModel = viewModel // Binding에 ViewModel 연결
        binding.lifecycleOwner = viewLifecycleOwner // LiveData와 UI 업데이트를 자동화
        return binding.root // Fragment의 루트 View 반환
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initializeDate() // 현재 날짜로 ViewModel 초기화
        viewModel.fetchTasks() // Firebase에서 작업 데이터를 불러옴

        setupRecyclerView() // RecyclerView 초기 설정
        observeViewModel() // ViewModel의 데이터 변경을 관찰

        // 캘린더에서 날짜를 선택했을 때 호출
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = "${year}년 ${month + 1}월 ${dayOfMonth}일" // 선택한 날짜
            viewModel.setDate(date) // 선택한 날짜를 ViewModel에 설정
        }

        // '새 작업 추가' 버튼
        binding.button3.setOnClickListener {
            val newTask = "새 작업" // 기본 입력값
            viewModel.addTask(newTask) // ViewModel을 통해 작업 추가
            binding.scheduleRecyclerView.smoothScrollToPosition(adapter.itemCount - 1) // RecyclerView를 새 작업으로 스크롤
        }

        // '체크된 작업 제거' 버튼
        binding.button4.setOnClickListener {
            viewModel.removeCheckedTasks() // ViewModel을 통해 체크된 작업 제거
        }
    }

    // RecyclerView 설정
    private fun setupRecyclerView() {
        adapter = ScheduleAdapter(
            mutableListOf(), // 초기 데이터는 빈 리스트
            { position, isChecked -> viewModel.updateTaskCheckedState(position, isChecked) }, // 체크 상태 변경 확인
            { position, text -> viewModel.updateTaskText(position, text) } // 텍스트 변경 확인
        )
        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext()) // 세로 스크롤 리스트로 설정
        binding.scheduleRecyclerView.adapter = adapter // RecyclerView에 어댑터 연결
    }

    // ViewModel의 LiveData를 관찰하고 UI를 업데이트
    private fun observeViewModel() {
        viewModel.tasksForDate.observe(viewLifecycleOwner, Observer { tasks ->
            adapter.updateTasks(tasks) // 리스트가 변경되면 어댑터에 전달
        })

        viewModel.selectedDate.observe(viewLifecycleOwner, Observer { date ->
            binding.scheduleTitle.text = "$date 할 일" // 선택된 날짜를 제목으로 표시
        })
    }

    // 뷰가 파괴될 때 호출
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
