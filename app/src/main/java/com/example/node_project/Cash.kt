// View (UI)
// UI구성, CashViewModel.kt로 데이터 불러옴
// RecyclerView로 목록 표시하고 실시간으로 업데이트
// CashViewModel.kt에 데이터 요청하고, 목록 누르면 화면 이동하고, +버튼 다룸

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
    private val itemList = mutableListOf<Pair<String, String>>()  // (날짜, 키) 저장할 리스트

    private val viewModel: CashViewModel by viewModels()  // ViewModel로 데이터 처리


    // Fragment가 화면에 생성되면 호출
    override fun onCreateView(    // container은 부모뷰임 레이아웃 포함될 모든 컨테이너 참조함
        inflater: LayoutInflater, container: ViewGroup?, // inflater(xml을 실제 UI로 변환)
        savedInstanceState: Bundle? // 전에 저장된 화면 데이터 저장해놓고 있음
    ): View? {
        // fragment_cash.xml을 view로 변환
        val view = inflater.inflate(R.layout.fragment_cash, container, false)

        // RecyclerView 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)  // R로 리소스 파일 참조
        recyclerView.layoutManager = LinearLayoutManager(requireContext())  // LinearLayoutManager로 세로로 스크롤되는 리스트 설정


        // 어댑터로 리스트 항목 누르면 처리할 동작
        adapter = CashAdapter(itemList) { pair ->
            val (date, key) = pair  // 선택한 목록의 날짜와 키 분리해서
            val bundle = Bundle().apply {
                putString("date", date)  // 날짜는 번들에 담아 전달
                putString("key", key)    // 키는 번들에 담아 전달
            }
            // CashItemFragment로 화면 이동해서 작성했던 데이터들 보여줄거임
            findNavController().navigate(R.id.action_cash_to_cashItemFragment, bundle)
        }


        // RecyclerView에 어댑터 설정
        recyclerView.adapter = adapter

        // + 버튼 클릭하면 목록 새로 추가하는 화면으로 이동
        val btnAdd: Button = view.findViewById(R.id.addButton)
        btnAdd.setOnClickListener {
            // 새로운 목록 추가 화면으로 이동
            findNavController().navigate(R.id.action_cash_to_cashItemFragment)
        }


        // ViewModel에서 아이템 목록 데이터를 불러옴
        viewModel.loadItems()

        // ViewModel에서 itemList의 LiveData를 이용해서 UI를 업데이트 함
        viewModel.itemList.observe(viewLifecycleOwner) { newList ->
            val oldListSize = itemList.size  // 기존 리스트 크기 저장
            itemList.clear()  // 기존 리스트는 초기화하고,
            itemList.addAll(newList)  // 새로운 리스트 데이터로 갱신함


            // 데이터 변경에 따라서 RecyclerView 업데이트
            if (newList.size > oldListSize) {
                // 목록의 데이터가 추가되면 그거만 반영
                adapter.notifyItemInserted(newList.size - 1)
            } else if (newList.size < oldListSize) {
                // 목록의 데이터가 삭제되어도 그거만 반영,
                adapter.notifyItemRemoved(newList.size)
            } else {
                // 목록의 데이터 내용이 변경되면 전체 항목 범위를 반영
                adapter.notifyItemRangeChanged(0, itemList.size)
            }
        }

        return view
    }
}
