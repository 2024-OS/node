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
    private val itemList = mutableListOf<Pair<String, String>>() // (고유 키, 날짜)

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cash, container, false)

        database = FirebaseDatabase.getInstance()
        myRef = database.reference.child("cash_items")

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = CashAdapter(itemList, onItemClick = { key ->
            val bundle = Bundle().apply {
                putString("key", key) // 고유 키 전달
            }
            findNavController().navigate(R.id.action_cash_to_cashItemFragment, bundle)
        })

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        loadDataFromFirebase() // Firebase에서 데이터 로드

        // + 버튼 클릭 시 CashItemFragment로 이동
        view.findViewById<Button>(R.id.addButton).setOnClickListener {
            findNavController().navigate(R.id.action_cash_to_cashItemFragment)
        }

        return view
    }

    private fun loadDataFromFirebase() {
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                for (child in snapshot.children) {
                    val key = child.key.orEmpty()
                    val cashItem = child.getValue(CashItem::class.java)
                    if (cashItem != null) {
                        itemList.add(Pair(key, cashItem.date))
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "데이터 로드 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
