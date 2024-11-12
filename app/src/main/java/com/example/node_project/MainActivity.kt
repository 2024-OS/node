package com.example.node_project

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 시스템 바
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.navController
        } else {
            throw IllegalStateException("NavHostFragment가 초기화되지 않았습니다.")
        }

        // 클릭시 이동
        findViewById<FrameLayout>(R.id.calendarFragment).setOnClickListener { // 캘린더 버튼 클릭 시
            navController.navigate(R.id.fragment_calender) //fragment_calender로 이동
        }

        findViewById<FrameLayout>(R.id.placeFragment).setOnClickListener {  // 장소선정 버튼 클릭 시
            navController.navigate(R.id.planList)  // planList 프래그먼트로 이동
        }

        findViewById<FrameLayout>(R.id.accountFragment).setOnClickListener {  // 회계내역 버튼 클릭 시
            navController.navigate(R.id.cash)  // cash 프래그먼트로 이동
        }

        findViewById<FrameLayout>(R.id.budgetFragment).setOnClickListener {  // 예산안 버튼 클릭 시
            navController.navigate(R.id.budget)  // budget 프래그먼트로 이동
        }
    }
}
