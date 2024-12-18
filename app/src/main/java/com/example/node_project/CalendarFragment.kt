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

    private lateinit var adapter: CalendarScheduleAdapter

    //Fragment의 View를 생성
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalenderBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel // Data Binding 설정
        binding.lifecycleOwner = viewLifecycleOwner // LiveData 관찰
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initializeDate() // 초기 날짜 설정
        viewModel.fetchTasks() // Firebase 데이터 불러오기

        setupRecyclerView() // RecyclerView 초기화
        observeViewModel() // ViewModel의 LiveData 관찰 및 UI 업데이트

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            viewModel.setDate(date) // 선택된 날짜를 ViewModel에 전달
        }

        // 추가
        binding.button3.setOnClickListener {
            viewModel.addTask("새 작업")   // ViewModel을 통해 새 작업 추가
            binding.scheduleRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }

        // 삭제
        binding.button4.setOnClickListener {
            viewModel.removeCheckedTasks()  // ViewModel을 통해 체크된 작업 삭제
        }
    }

    //RecyclerView와 어댑터를 초기화
    private fun setupRecyclerView() {
        adapter = CalendarScheduleAdapter(
            mutableListOf(),
            { position, isChecked -> viewModel.updateTaskCheckedState(position, isChecked) },   // 체크 상태 업데이트
            { position, text -> viewModel.updateTaskText(position, text) }  // 텍스트 업데이트
        )
        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.scheduleRecyclerView.adapter = adapter  // 어댑터 연결
    }

    //ViewModel의 LiveData를 관찰하고 UI를 업데이트
    private fun observeViewModel() {
        //날짜에 해당하는 작업 리스트 관찰 -> RecyclerView 업데이트
        viewModel.tasksForDate.observe(viewLifecycleOwner, Observer { tasks ->
            adapter.updateTasks(tasks)
        })
        // 선택된 날짜 관찰 -> 상단 제목 텍스트 업데이트
        viewModel.selectedDate.observe(viewLifecycleOwner, Observer { date ->
            binding.scheduleTitle.text = getString(R.string.schedule_title, date ?: "날짜 없음")
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
