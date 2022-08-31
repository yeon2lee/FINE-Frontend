package com.fine_app.ui.myPage

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fine_app.databinding.ActivitySettingBinding
import com.fine_app.ui.myPage.Profile
import com.fine_app.ui.myPage.ServiceCreator
import com.fine_app.ui.myPage.UpdateProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    lateinit var userInfo: SharedPreferences
    var userId by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)
        userId = userInfo.getString("userInfo", "2")!!.toLong()

        binding.settingProfileTv.setOnClickListener {
            val intent = Intent(this, UpdateProfileActivity::class.java)
            startActivity(intent)
        }

        binding.tvUserLogout.setOnClickListener {
            logout()
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            //val intent = Intent(this, SignUpActivity::class.java)
            //startActivity(intent)
        }

        binding.tvSecession.setOnClickListener {
            userSecession()
            //val intent = Intent(this, LoginActivity::class.java)
            //startActivity(intent)
        }
    }

    // 로그아웃
    private fun logout() {
        val call: Call<Long> = ServiceCreator.service.logout() //

        call.enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SettingActivity, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    userInfo.edit().putString("userInfo", "0")
                } else {
                    //Toast.makeText(this@SettingActivity, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                    userInfo.edit().putString("userInfo", "0")

                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Toast.makeText(this@SettingActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
        finish()
    }

    // 회원 탈퇴
    private fun userSecession() {
        val call: Call<Profile> = ServiceCreator.service.userSecession(userId.toLong()) // todo 유저 아이디 불러오기

        call.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SettingActivity, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this@SettingActivity, "회원 탈퇴 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Toast.makeText(this@SettingActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
        finish()
    }
}