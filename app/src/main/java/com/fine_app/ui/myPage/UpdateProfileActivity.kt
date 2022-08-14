package com.fine_app.ui.MyPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.fine_app.databinding.ActivityMypageProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfileActivity : AppCompatActivity() {
    lateinit var _binding: ActivityMypageProfileBinding
    var userId: Long = 2
    lateinit var userData: Profile
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMypageProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.etNickname.setText(intent.getStringExtra("nickname"))
        binding.etInfo.setText(intent.getStringExtra("intro"))

        binding.btnBefore.setOnClickListener {
            finish()
        }
    }

    fun onClick(v: View?) {
        editProfile()
    }

    private fun editProfile() {
        val requestProfileData = RequestProfileData(
            nickname = binding.etNickname.text.toString(),
            intro = binding.etInfo.text.toString()
        )
        userId = 1

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