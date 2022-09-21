package com.fine_app.ui.myPage

import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.fine_app.R
import com.fine_app.databinding.ActivityMypageProfileBinding
import com.fine_app.ui.myPage.ServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class UpdateProfileActivity : AppCompatActivity() {
    lateinit var _binding: ActivityMypageProfileBinding
    lateinit var userInfo: SharedPreferences
    var userId by Delegates.notNull<Long>()
    lateinit var userData: Profile
    var checked = arrayOfNulls<String>(3)
    var size = 0
    var imageNum = 1
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMypageProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)
        userId = userInfo.getString("userInfo", "2")!!.toLong()

        binding.etInfo.setText(intent.getStringExtra("intro"))

        binding.btnBefore.setOnClickListener {
            finish()
        }

        binding.profileSettingImage1Iv.setOnClickListener {
            if (binding.profileSettingImage1Iv.background==null) {
                binding.profileSettingImage1Iv.setBackgroundResource(R.drawable.profile_image_border)
                imageNum = 1
            } else {
                binding.profileSettingImage1Iv.setBackgroundResource(0)
            }
        }
        binding.profileSettingImage2Iv.setOnClickListener {
            if (binding.profileSettingImage2Iv.background==null) {
                binding.profileSettingImage2Iv.setBackgroundResource(R.drawable.profile_image_border)
                imageNum = 2
            } else {
                binding.profileSettingImage2Iv.setBackgroundResource(0)
            }
        }
        binding.profileSettingImage3Iv.setOnClickListener {
            if (binding.profileSettingImage3Iv.background==null) {
                binding.profileSettingImage3Iv.setBackgroundResource(R.drawable.profile_image_border)
                imageNum = 3
            } else {
                binding.profileSettingImage3Iv.setBackgroundResource(0)
            }
        }
        binding.profileSettingImage4Iv.setOnClickListener {
            if (binding.profileSettingImage4Iv.background==null) {
                binding.profileSettingImage4Iv.setBackgroundResource(R.drawable.profile_image_border)
                imageNum = 4
            } else {
                binding.profileSettingImage4Iv.setBackgroundResource(0)
            }
        }
        binding.profileSettingImage5Iv.setOnClickListener {
            if (binding.profileSettingImage5Iv.background==null) {
                binding.profileSettingImage5Iv.setBackgroundResource(R.drawable.profile_image_border)
                imageNum = 5
            } else {
                binding.profileSettingImage5Iv.setBackgroundResource(0)
            }
        }
        binding.profileSettingImage6Iv.setOnClickListener {
            if (binding.profileSettingImage6Iv.background==null) {
                binding.profileSettingImage6Iv.setBackgroundResource(R.drawable.profile_image_border)
                imageNum = 5
            } else {
                binding.profileSettingImage6Iv.setBackgroundResource(0)
            }
        }

    }

    fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btnOk -> editProfile()

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


    private fun editProfile() {
        val requestProfileData = RequestProfileData(
            userImageNum = imageNum,
            nickname = intent.getStringExtra("nickname").toString(),
            intro = binding.etInfo.text.toString(),
            keyword1 = intent.getStringExtra("keyword1").toString(),
            keyword2 = intent.getStringExtra("keyword2").toString(),
            keyword3 = checked[0].toString()
        )

        val call: Call<Profile> = ServiceCreator.service.editProfile(userId, requestProfileData)

        call.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    userData = response.body()!!
                } else {
                    Toast.makeText(this@UpdateProfileActivity, "프로필 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Toast.makeText(this@UpdateProfileActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }

        })

        finish()
    }
}