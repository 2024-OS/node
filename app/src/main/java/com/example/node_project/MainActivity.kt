package com.example.node_project

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Firebase 초기화
        if (FirebaseApp.getApps(this).isEmpty()) // firebase가 초기화 안되면
        {
            FirebaseApp.initializeApp(this) // 초기화하기
        }


        // 내비게이션 바 영역
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // 시스템 바 가져오기
            // 영역 맞춰서 패딩 조정하기
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 내비게이션 컨트롤러 초기화
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.navController // 내비게이션 컨트롤러 가져오기
        } else {
            throw IllegalStateException("NavHostFragment가 초기화되지 않았습니다.") // 없으면 예외처리
        }

        // 클릭하면 이동
        findViewById<FrameLayout>(R.id.calendarFragment).setOnClickListener { // 캘린더 버튼 클릭하면
            navController.navigate(R.id.fragment_calender) //fragment_calender로 이동
        }

        findViewById<FrameLayout>(R.id.placeFragment).setOnClickListener {  // 장소선정 버튼 클릭하면
            navController.navigate(R.id.planList)  // planList로 이동
        }

        findViewById<FrameLayout>(R.id.accountFragment).setOnClickListener {  // 회계내역 버튼 클릭하면
            navController.navigate(R.id.cash)  // cash로 이동
        }

        findViewById<FrameLayout>(R.id.budgetFragment).setOnClickListener {  // 예산안 버튼 클릭하면
            navController.navigate(R.id.budget)  // budget로 이동
        }
    }
}
