package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController

class Cash : Fragment() {

    private lateinit var adapter: CashAdapter
    private val itemList = mutableListOf<CashItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cash, container, false)

        // RecyclerView 설정
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = CashAdapter(itemList) { cashItem ->
            // 클릭 시 행동
            val bundle = Bundle().apply {
                putString("date", cashItem.date)
                putString("amount", cashItem.amount)
                putString("content", cashItem.content)
            }
            findNavController().navigate(R.id.action_cash_to_cashItemFragment, bundle)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // + 버튼 클릭 리스너
        val addButton = view.findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            // 버튼 클릭 시 CashItemFragment로 이동
            findNavController().navigate(R.id.action_cash_to_cashItemFragment)
        }

        // 결과 받기 (Fragment 간 데이터 전송)
        parentFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { _, bundle ->
            val date = bundle.getString("date")
            val amount = bundle.getString("amount")
            val content = bundle.getString("content")

            // 값이 있으면 아이템 추가
            if (!date.isNullOrEmpty()) {
                addItem(CashItem(date, amount ?: "", content ?: ""))
            }
        }

        return view
    }

    private fun addItem(item: CashItem) {
        itemList.add(item)
        adapter.notifyDataSetChanged()  // RecyclerView 업데이트
    }
}
