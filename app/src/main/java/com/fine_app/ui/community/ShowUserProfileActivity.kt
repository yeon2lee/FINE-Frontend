package com.fine_app.ui.community

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.fine_app.databinding.ActivityShowUserProfileBinding
import com.fine_app.ui.MyPage.Profile
import com.fine_app.ui.MyPage.ServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class ShowUserProfileActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityShowUserProfileBinding
    private var userId by Delegates.notNull<Long>()
    lateinit var userData: Profile
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId= intent.getLongExtra("memberId", 1)
        getUserProfile()

        _binding = ActivityShowUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun getUserProfile() {

        val call: Call<Profile> = ServiceCreator.service.getMyProfile(userId)

        call.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    userData = response.body()!!
                    binding.profileUserNicknameTv.text = userData.nickname
                    binding.profileUserIntroTv.text = userData.intro
                } else {
                    Toast.makeText(this@ShowUserProfileActivity, "프로필 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Toast.makeText(this@ShowUserProfileActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }

        })

    }
}