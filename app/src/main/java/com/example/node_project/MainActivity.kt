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

        // 시스템 바 패딩 설정 (Edge-to-Edge 디자인)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // NavHostFragment에서 NavController 초기화
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.navController
        } else {
            throw IllegalStateException("NavHostFragment가 초기화되지 않았습니다.")
        }

        // 클릭 리스너 설정
        findViewById<FrameLayout>(R.id.calendarFragment).setOnClickListener {
            navController.navigate(R.id.fragment_calender)
        }

        findViewById<FrameLayout>(R.id.placeFragment).setOnClickListener {
            navController.navigate(R.id.planList)
        }

        findViewById<FrameLayout>(R.id.accountFragment).setOnClickListener {
            navController.navigate(R.id.cash)
        }

        findViewById<FrameLayout>(R.id.budgetFragment).setOnClickListener {
            navController.navigate(R.id.budget)
        }
    }
}
