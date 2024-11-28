package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.viewModels

class Cash : Fragment() {

    private lateinit var adapter: CashAdapter
    private val itemList = mutableListOf<Pair<String, String>>()

    private val viewModel: CashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cash, container, false)

        // RecyclerView 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = CashAdapter(itemList) { pair ->
            val (date, key) = pair
            val bundle = Bundle().apply {
                putString("date", date)
                putString("key", key)
            }
            findNavController().navigate(R.id.action_cash_to_cashItemFragment, bundle)
        }

        recyclerView.adapter = adapter

        // "추가" 버튼 클릭 시 데이터 추가 화면으로 이동
        val btnAdd: Button = view.findViewById(R.id.addButton)
        btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_cash_to_cashItemFragment)
        }

        // ViewModel에서 실시간 데이터 로드
        viewModel.loadItems()

        // 아이템 목록 LiveData를 관찰하여 UI 업데이트
        viewModel.itemList.observe(viewLifecycleOwner) { newList ->
            val oldListSize = itemList.size
            itemList.clear()
            itemList.addAll(newList)

            // 데이터가 변경된 항목만 갱신
            if (newList.size > oldListSize) {
                // 새로운 아이템이 추가된 경우
                adapter.notifyItemInserted(newList.size - 1)
            } else if (newList.size < oldListSize) {
                // 아이템이 삭제된 경우
                adapter.notifyItemRemoved(newList.size)
            } else {
                // 아이템의 내용이 변경된 경우
                adapter.notifyItemRangeChanged(0, itemList.size)
            }
        }

        return view
    }
}
