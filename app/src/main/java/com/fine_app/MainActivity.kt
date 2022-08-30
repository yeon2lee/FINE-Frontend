package com.fine_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.fine_app.databinding.ActivityMainBinding
import com.fine_app.ui.home.FriendRecommendFragment
import com.fine_app.ui.home.HomeFragment
import com.fine_app.ui.myPage.LoginActivity
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private var userId by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userInfo = getSharedPreferences("userInfo", 0)
        userInfo.edit().putString("userInfo", "0").apply()
        userId = userInfo.getString("userInfo", "2")!!.toLong()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인 안 했을 때
        if (userId == 0.toLong()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val navView: BottomNavigationView = binding.navView


        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_chatList, R.id.navigation_chatList, R.id.navigation_community, R.id.navigation_myPage
            )
        )

        navView.setupWithNavController(navController)

    }

    override fun onResume() {
        super.onResume()
        userId = userInfo.getString("userInfo", "2")!!.toLong()
    }


}
