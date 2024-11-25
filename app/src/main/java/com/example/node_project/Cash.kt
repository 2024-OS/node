package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*

class Cash : Fragment() {
    private lateinit var adapter: CashAdapter
    private val itemList = mutableListOf<Pair<String, String>>() // 날짜와 키를 함께 저장

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    // Fragment가 생성될 때 호출되는 메소드
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃을 인플레이트합니다.
        val view = inflater.inflate(R.layout.fragment_cash, container, false)

        // Firebase 초기화: FirebaseDatabase 인스턴스를 사용하여 Realtime Database의 cash_items를 참조
        database = FirebaseDatabase.getInstance()
        myRef = database.reference.child("cash_items")

        // RecyclerView 설정: RecyclerView는 cash_item을 표시하기 위해 사용됩니다.
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        // CashAdapter는 RecyclerView의 데이터를 관리하고 클릭 시 동작을 처리합니다.
        adapter = CashAdapter(itemList, onItemClick = { pair ->
            val (date, key) = pair // 날짜와 고유 키를 가져옴
            val bundle = Bundle().apply {
                putString("date", date) // 선택된 날짜 전달
                putString("key", key)  // 해당 항목의 고유 키 전달
            }
            // 클릭된 항목에 대해 다른 화면으로 이동
            findNavController().navigate(R.id.action_cash_to_cashItemFragment, bundle)
        })

        // RecyclerView의 레이아웃 매니저 설정 (세로 리스트 형태로 표시)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Firebase에서 데이터를 불러오는 함수 호출
        loadDataFromFirebase()

        // + 버튼 클릭 시 새로운 데이터 추가 화면으로 이동
        view.findViewById<Button>(R.id.addButton).setOnClickListener {
            findNavController().navigate(R.id.action_cash_to_cashItemFragment)
        }

        return view
    }

    // Firebase에서 데이터를 로드하는 메소드
    private fun loadDataFromFirebase() {
        // "date" 필드를 기준으로 정렬하여 데이터를 로드
        myRef.orderByChild("date")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    itemList.clear() // 기존 아이템을 비운 후 새로 추가

                    // Firebase에서 받은 데이터를 itemList에 추가
                    for (child in snapshot.children) {
                        val item = child.getValue(CashModel::class.java) // CashItem 객체로 변환
                        val key = child.key // 해당 항목의 고유 키
                        if (item != null && key != null) {
                            itemList.add(Pair(item.date, key)) // 날짜와 키를 함께 저장
                        }
                    }

                    // 데이터가 갱신되었으므로 RecyclerView를 업데이트
                    adapter.notifyDataSetChanged()
                }

                // Firebase에서 데이터 로드 실패 시 호출되는 메소드
                override fun onCancelled(error: DatabaseError) {
                    // 에러 발생 시 사용자에게 실패 메시지 표시
                    Toast.makeText(requireContext(), "데이터 로드 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
