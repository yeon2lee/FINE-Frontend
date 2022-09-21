package com.fine_app.ui.myPage

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
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
    var imageNum = 0

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.etUserPwd2.addTextChangedListener(object: TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                val pwdConfirm = binding.etUserPwd2.text.toString()
//                if (!binding.etUserPwd.text.toString().equals(pwdConfirm)) {
//                    binding.alertPassword.setText("비밀번호가 일치하지 않습니다.")
//                } else {
//                    binding.alertPassword.setText("비밀번호가 일치합니다.")
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                TODO("Not yet implemented")
//            }
//        })

        binding.imgProfile1.setOnClickListener {
            if (binding.imgProfile1.background==null) {
                binding.imgProfile1.setBackgroundResource(R.drawable.profile_image_border)
                imageNum = 1
            } else {
                binding.imgProfile1.setBackgroundResource(0)
            }
        }
        binding.imgProfile2.setOnClickListener {
            if (binding.imgProfile2.background==null) {
                binding.imgProfile2.setBackgroundResource(R.drawable.profile_image_border)
                imageNum = 2
            } else {
                binding.imgProfile2.setBackgroundResource(0)
            }
        }
        binding.imgProfile3.setOnClickListener {
            if (binding.imgProfile3.background==null) {
                binding.imgProfile3.setBackgroundResource(R.drawable.profile_image_border)
                imageNum = 3
            } else {
                binding.imgProfile3.setBackgroundResource(0)
            }
        }
        binding.imgProfile4.setOnClickListener {
            if (binding.imgProfile4.background==null) {
                binding.imgProfile4.setBackgroundResource(R.drawable.profile_image_border)
                imageNum = 4
            } else {
                binding.imgProfile4.setBackgroundResource(0)
            }
        }
        binding.imgProfile5.setOnClickListener {
            if (binding.imgProfile5.background==null) {
                binding.imgProfile5.setBackgroundResource(R.drawable.profile_image_border)
                imageNum = 5
            } else {
                binding.imgProfile5.setBackgroundResource(0)
            }
        }
        binding.imgProfile6.setOnClickListener {
            if (binding.imgProfile6.background==null) {
                binding.imgProfile6.setBackgroundResource(R.drawable.profile_image_border)
                imageNum = 6
            } else {
                binding.imgProfile6.setBackgroundResource(0)
            }
        }
    }

    fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btnSignUp -> signUp()
                // 키워드 클릭 체크
                R.id.signup_keyword1 ->  {
                    checked[size++] = binding.signupKeyword1.text.toString()
                    binding.signupKeyword1.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword2 ->  {
                    checked[size++] = binding.signupKeyword2.text.toString()
                    binding.signupKeyword2.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword3 -> {
                    checked[size++] = binding.signupKeyword3.text.toString()
                    binding.signupKeyword3.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword4 ->  {
                    checked[size++] = binding.signupKeyword4.text.toString()
                    binding.signupKeyword4.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword5 ->  {
                    checked[size++] = binding.signupKeyword5.text.toString()
                    binding.signupKeyword5.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword6 ->  {
                    checked[size++] = binding.signupKeyword6.text.toString()
                    binding.signupKeyword6.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword7 ->  {
                    checked[size++] = binding.signupKeyword7.text.toString()
                    binding.signupKeyword7.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword8 ->  {
                    checked[size++] = binding.signupKeyword8.text.toString()
                    binding.signupKeyword8.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword9 ->  {
                    checked[size++] = binding.signupKeyword9.text.toString()
                    binding.signupKeyword9.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword10 -> {
                    checked[size++] = binding.signupKeyword10.text.toString()
                    binding.signupKeyword10.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword11 -> {
                    checked[size++] = binding.signupKeyword11.text.toString()
                    binding.signupKeyword11.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.signup_keyword12 -> {
                    checked[size++] = binding.signupKeyword12.text.toString()
                    binding.signupKeyword12.setTextColor(Color.parseColor("#6DB33F"))
                }

                R.id.signup_keyword13 -> {
                    checked[size++] = binding.signupKeyword13.text.toString()
                    binding.signupKeyword13.setTextColor(Color.parseColor("#6DB33F"))
                }

                R.id.signup_keyword14 -> {
                    checked[size++] = binding.signupKeyword14.text.toString()
                    binding.signupKeyword14.setTextColor(Color.parseColor("#6DB33F"))
                }
            }
        }
    }

    private fun signUp() {
        val requestAuthData = RequestAuthData(
            userId = binding.etUserId.text.toString(),
            password = binding.etUserPwd.text.toString(),
            nickname = binding.etNickname2.text.toString(),
            userImageNum = imageNum,
            intro = binding.etIntro.text.toString(),
            keyword1 = "한이음대학교",
            keyword2 = "서울",
            keyword3 = checked[0].toString(),
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