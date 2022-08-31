package com.fine_app.ui.myPage

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.fine_app.R
import com.fine_app.databinding.ActivitySignUpBinding
import com.fine_app.ui.myPage.Profile
import com.fine_app.ui.myPage.RequestAuthData
import com.fine_app.ui.myPage.ServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    lateinit var _binding: ActivitySignUpBinding
    lateinit var userData: Profile
    //var checked = Array(13){i -> false}
    var checked = arrayOfNulls<String>(3)
    var size = 0

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btnSignUp -> signUp()
                // 키워드 클릭 체크
                R.id.signup_keyword1 ->  {
                    checked[size++] = "1"
                    binding.signupKeyword1.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword2 ->  {
                    checked[size++] = "2"
                    binding.signupKeyword2.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword3 -> {
                    checked[size++] = "3"
                    binding.signupKeyword3.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword4 ->  {
                    checked[size++] = "4"
                    binding.signupKeyword4.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword5 ->  {
                    checked[size++] = "5"
                    binding.signupKeyword5.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword6 ->  {
                    checked[size++] = "6"
                    binding.signupKeyword6.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword7 ->  {
                    checked[size++] = "7"
                    binding.signupKeyword7.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword8 ->  {
                    checked[size++] = "8"
                    binding.signupKeyword8.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword9 ->  {
                    checked[size++] = "9"
                    binding.signupKeyword9.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword10 -> {
                    checked[size++] = "10"
                    binding.signupKeyword10.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword11 -> {
                    checked[size++] = "11"
                    binding.signupKeyword11.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword12 -> {
                    checked[size++] = "12"
                    binding.signupKeyword12.setTextColor(Color.parseColor("#6DB33F"))
                }
            }
        }

    }

    private fun signUp() {
        val requestAuthData = RequestAuthData(
            userId = binding.etUserId.text.toString(),
            password = binding.etUserPwd.text.toString(),
            nickname = binding.etUserId.text.toString(),
            keyword1 = checked[0].toString(),
            keyword2 = checked[1].toString(),
            keyword3 = checked[2].toString(),
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