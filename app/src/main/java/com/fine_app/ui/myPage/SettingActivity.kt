package com.fine_app.ui.myPage

import android.content.Intent
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

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    val userId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settingProfileTv.setOnClickListener {
            val intent = Intent(this, UpdateProfileActivity::class.java)
            startActivity(intent)
        }

        binding.tvSecession.setOnClickListener{
            userSecession()
        }

        binding.tvUserSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

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