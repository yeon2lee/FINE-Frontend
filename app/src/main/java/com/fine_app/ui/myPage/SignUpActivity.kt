package com.fine_app.ui.myPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.fine_app.databinding.ActivitySignUpBinding
import com.fine_app.ui.MyPage.Profile
import com.fine_app.ui.MyPage.RequestAuthData
import com.fine_app.ui.MyPage.ServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    lateinit var _binding: ActivitySignUpBinding
    lateinit var userData: Profile
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onClick(v: View?) {
        signUp()
    }

    private fun signUp() {
        val requestAuthData = RequestAuthData(
            id = binding.etUserId.text.toString(),
            password = binding.etUserPwd.text.toString()
        )
        val pwdConfirm = binding.etUserPwd2.text.toString()
        if (requestAuthData.password.equals(pwdConfirm)) {
            val call: Call<Profile> = ServiceCreator.service.signUp(requestAuthData)

            call.enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        userData = response.body()!!
                    } else {
                        Toast.makeText(this@SignUpActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
                }

            })
            finish()
        } else {
            Toast.makeText(this@SignUpActivity, "비밀번호를 다시 입력해주세요", Toast.LENGTH_SHORT).show()
        }
    }
}