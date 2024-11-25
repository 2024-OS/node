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

class CalendarFragment : Fragment() {

    private val viewModel: CalendarViewModel by viewModels()
    private var _binding: FragmentCalenderBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalenderBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // 캘린더 날짜 선택 이벤트
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            viewModel.setDate(date) // 선택된 날짜를 ViewModel에 전달
        }

        // 추가 버튼 클릭 이벤트
        binding.button3.setOnClickListener {
            viewModel.addTask("새 작업")
        }

        // 삭제 버튼 클릭 이벤트
        binding.button4.setOnClickListener {
            viewModel.removeCheckedTasks()
        }
    }

    private fun setupRecyclerView() {
        // RecyclerView 어댑터 초기화 및 콜백 전달
        adapter = ScheduleAdapter(
            viewModel.getTasksForSelectedDate(),
            { position, isChecked ->
                viewModel.updateTaskCheckedState(position, isChecked) // 체크박스 상태 업데이트
            },
            { position, text ->
                viewModel.updateTaskText(position, text) // 텍스트 변경 이벤트 처리
            }
        )
        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.scheduleRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        // 날짜 변경 관찰
        viewModel.selectedDate.observe(viewLifecycleOwner, Observer {
            binding.scheduleTitle.text = it // 타이틀 업데이트
            updateTaskList() // 날짜에 맞는 할일 리스트 업데이트
        })

        // 할일 리스트 변경 관찰
        viewModel.scheduleData.observe(viewLifecycleOwner, Observer {
            updateTaskList() // 할일 리스트 변경 시 RecyclerView 갱신
        })
    }

    private fun updateTaskList() {
        // 현재 날짜에 해당하는 할일 리스트 가져오기
        val tasks = viewModel.getTasksForSelectedDate()
        adapter = ScheduleAdapter(
            tasks,
            { position, isChecked ->
                viewModel.updateTaskCheckedState(position, isChecked) // 체크박스 상태 업데이트
            },
            { position, text ->
                viewModel.updateTaskText(position, text) // 텍스트 변경 이벤트 처리
            }
        )
        binding.scheduleRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
