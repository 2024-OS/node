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

// Firebase에서 데이터를 RecyclerView에 표시하고,
// 새로운 목록을 추가할 수 있게 UI를 제공하는 iew

class Cash : Fragment() {
    private lateinit var adapter: CashAdapter   // RecyclerView에 데이터를 표시하는 Adapter
    private val itemList = mutableListOf<Pair<String, String>>()  // 날짜와 키를 저장

    private lateinit var database: FirebaseDatabase  // 실시간 파이어베이스 데이터
    private lateinit var myRef: DatabaseReference   // Firebas에서 'cash_items' 참조할 거


//////////////////////////////////////////////////////////////////////////////////////////
// fragment 뷰 생성하고 초기화

    // Fragment의 뷰가 생성될 때 호출될거임
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃 파일을 로드해서 뷰 생성 (인플레이트)
        val view = inflater.inflate(R.layout.fragment_cash, container, false)

        // Firebase 초기화: FirebaseDatabase 인스턴스를 가져오고 'cash_items'를 참조
        database = FirebaseDatabase.getInstance()
        myRef = database.reference.child("cash_items")

        // RecyclerView에서 날짜순으로 정렬하기
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())  // 리스트의 아이템들을 세로로 나열

        // 날짜와 키를 저장
        adapter = CashAdapter(itemList) { pair ->
            // RecyclerView 아이템을 클릭했을 때 날짜와 키를 전달
            val (date, key) = pair
            val bundle = Bundle().apply {
                putString("date", date)
                putString("key", key)
            }
            // 회계내역 목록 수정 화면으로 이동 (nav_main 이용)
            findNavController().navigate(R.id.action_cash_to_cashItemFragment, bundle)
        }

        // RecyclerView에 Adapter 설정
        recyclerView.adapter = adapter

        // Firebase에서 실시간으로 데이터 업데이트 하기
        listenToRealtimeUpdates()

        // + 버튼 클릭하면 목록 추가하는 화면으로 이동
        val btnAdd: Button = view.findViewById(R.id.addButton)
        btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_cash_to_cashItemFragment)
        }

        // Fragment의 뷰를 반환
        return view
    }


///////////////////////////////////////////////////////////////////////////////////////////////
// 실시간으로 데이터 업데이트하고 RecyclerView 갱신함

    // Firebase에서 실시간으로 데이터 업데이트 하기
    private fun listenToRealtimeUpdates() {
        // Firebase에서 날짜순으로 정렬해서 데이터 가져오기
        myRef.orderByChild("date").addValueEventListener(object : ValueEventListener {

            // 데이터가 변경될 때마다
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()  // 기존에 저장된 데이터를 초기화하고,

                // Firebase에서 데이터 순서대로 날짜와 키를 추출해서 itemList에 추가하기
                for (dataSnapshot in snapshot.children) {
                    val key = dataSnapshot.key
                    val cashItem = dataSnapshot.getValue(CashModel::class.java)  // 데이터 항목을 CashModel로 변환하기

                    // 데이터가 정상적으로 변환되었으면 리스트에 추가하기
                    if (cashItem != null) {
                        itemList.add(Pair(cashItem.date, key ?: ""))  // 날짜와 키를 함께 리스트에 추가하기
                    }
                }

                // 데이터가 변경되었다고 Adapter에 데이터 갱신되었다고 알리기
                adapter.notifyDataSetChanged()
            }

            // Firebase에서 데이터 로드가 실패하면 호출됨
            override fun onCancelled(error: DatabaseError) {
                // 에러 메시지를 Toast로 표시함
                Toast.makeText(context, "데이터 로드 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
