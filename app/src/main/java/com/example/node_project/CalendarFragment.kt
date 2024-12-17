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

        setupRecyclerView()
        observeViewModel()

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            viewModel.setDate(date)
        }

        binding.button3.setOnClickListener {
            viewModel.addTask("새 작업")
            binding.scheduleRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }

        binding.button4.setOnClickListener {
            viewModel.removeCheckedTasks()
        }
    }

    private fun setupRecyclerView() {
        adapter = ScheduleAdapter(
            mutableListOf(),
            { position, isChecked -> viewModel.updateTaskCheckedState(position, isChecked) },
            { position, text -> viewModel.updateTaskText(position, text) }
        )
        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.scheduleRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.tasksForDate.observe(viewLifecycleOwner, Observer { tasks ->
            adapter.updateTasks(tasks)
        })

        viewModel.selectedDate.observe(viewLifecycleOwner, Observer { date ->
            binding.scheduleTitle.text = getString(R.string.schedule_title, date ?: "날짜 없음")
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
