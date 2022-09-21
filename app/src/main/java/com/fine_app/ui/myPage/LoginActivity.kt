package com.fine_app.ui.myPage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fine_app.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var userData: Profile
    lateinit var userInfo: SharedPreferences
    var userId by Delegates.notNull<Long>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.loginOkBtn.setOnClickListener {
            userLogin()
            Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
        }

        binding.loginSignupTv.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun userLogin() {
        val requestLoginData = RequestLoginData(
            id = binding.loginIdEt.text.toString(),
            password = binding.loginPwdEt.text.toString(),
        )

        val call: Call<Profile> = ServiceCreator.service.userLogin(requestLoginData)

        call.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    userData = response.body()!!
                    userInfo.edit().putString("userInfo", userData.id.toString()).apply()
                    finish()
                } else {
                    // 로그인 실패
                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }

        })



    }

}

