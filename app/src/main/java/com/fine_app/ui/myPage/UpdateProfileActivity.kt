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

        binding.etNickname.setText(intent.getStringExtra("nickname"))
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
                imageNum = 4
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
                R.id.profile_keyword1 ->  {
                    checked[size++] = "1"
                    binding.profileKeyword1.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.profile_keyword2 ->  {
                    checked[size++] = "2"
                    binding.profileKeyword2.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.profile_keyword3 -> {
                    checked[size++] = "3"
                    binding.profileKeyword3.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.profile_keyword4 ->  {
                    checked[size++] = "4"
                    binding.profileKeyword4.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.profile_keyword5 ->  {
                    checked[size++] = "5"
                    binding.profileKeyword5.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.profile_keyword6 ->  {
                    checked[size++] = "6"
                    binding.profileKeyword6.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.profile_keyword7 ->  {
                    checked[size++] = "7"
                    binding.profileKeyword7.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.profile_keyword8 ->  {
                    checked[size++] = "8"
                    binding.profileKeyword8.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.profile_keyword9 ->  {
                    checked[size++] = "9"
                    binding.profileKeyword9.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.profile_keyword10 -> {
                    checked[size++] = "10"
                    binding.profileKeyword10.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.profile_keyword11 -> {
                    checked[size++] = "11"
                    binding.profileKeyword11.setTextColor(Color.parseColor("#6DB33F"))
                }
                R.id.profile_keyword12 -> {
                    checked[size++] = "12"
                    binding.profileKeyword12.setTextColor(Color.parseColor("#6DB33F"))
                }
            }
        }

    }


    private fun editProfile() {
        val requestProfileData = RequestProfileData(
            userImageNum = imageNum,
            nickname = binding.etNickname.text.toString(),
            intro = binding.etInfo.text.toString(),
            keyword1 = checked[0].toString(),
            keyword2 = checked[1].toString(),
            keyword3 = checked[2].toString()

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