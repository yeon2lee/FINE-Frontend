package com.fine_app.ui.MyPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.fine_app.R
import com.fine_app.databinding.ActivityMypageProfileBinding
import com.fine_app.databinding.FragmentMypageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfileActivity : AppCompatActivity() {
    lateinit var _binding: ActivityMypageProfileBinding
    var userId: Long = 0
    lateinit var userData: Profile
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMypageProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.etNickname.setText(intent.getStringExtra("nickname"))
        binding.etInfo.setText(intent.getStringExtra("info"))

    }

    private fun onClick(view: View) {
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
                    userData = response.body()?.let {
                        Profile(
                            id = it.id,
                            nickname = it.nickname,
                            email = it.email,
                            userIntroduction = it.userIntroduction,
                            userUniversity = it.userUniversity,
                            userCollege = it.userCollege,
                            userPhoneNumber = it.userPhoneNumber,
                            userResidence = it.userResidence,
                            level = it.level,
                            report = it.report
                        )
                    } ?: Profile(0, "user", "", "", "", "", "", "", "", 0)

                    //binding.etNickname.setText(userData.nickname)
                    //binding.etInfo.setText(userData.userIntroduction)

                    Toast.makeText(this@UpdateProfileActivity,"성공", Toast.LENGTH_SHORT).show()
                    val data = response.body()?.nickname
                        Toast.makeText(this@UpdateProfileActivity, "${data}님 반갑습니다!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@UpdateProfileActivity, "프로필 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Toast.makeText(this@UpdateProfileActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }

        })

    }
}