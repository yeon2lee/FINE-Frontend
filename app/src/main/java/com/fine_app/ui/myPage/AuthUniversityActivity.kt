package com.fine_app.ui.myPage

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fine_app.databinding.ActivityAuthUniversityBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import kotlin.properties.Delegates

class AuthUniversityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthUniversityBinding
    lateinit var search: Intent
    lateinit var link: String
    lateinit var userInfo: SharedPreferences
    var userId by Delegates.notNull<Long>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthUniversityBinding.inflate(layoutInflater)

        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)
        userId = userInfo.getString("userInfo", "2")!!.toLong()

        setContentView(binding.root)

        binding.searchUniversityView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            //검색 버튼 눌렀을 때 호출
            override fun onQueryTextSubmit(p0: String): Boolean {
                search = Intent(this@AuthUniversityActivity, SearchUniversityActivity::class.java)
                search.putExtra("query", binding.searchUniversityView.query.toString())
                startActivityForResult(search, 1000)
                return true
            }
            //텍스트 입력과 동시에 호출
            override fun onQueryTextChange(p0: String): Boolean {

                return true
            }
        })

        binding.authUniversitySendMessage.setOnClickListener{
            sendMail()
        }

        binding.authUniversityReSendMessage.setOnClickListener{
            sendMail()
        }

        binding.authUniversityOk.setOnClickListener{
            verifyUniversityAuth()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.searchUniversityView.setQuery(data!!.getStringExtra("schoolName"), false)
        link = "@" + data!!.getStringExtra("link")
        binding.universityLink.setText(link)
    }

    private fun sendMail() {
        var address = binding.authUniversityMailEt.text.toString() + link
        val call: Call<Long> = ServiceCreator.service.sendMail(address)

        call.enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AuthUniversityActivity, "인증번호 전송 성공", Toast.LENGTH_SHORT).show()
                } else {
                    //Toast.makeText(this@AuthUniversityActivity, "인증번호 전송 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Toast.makeText(this@AuthUniversityActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()

            }
        })

    }

    // 인증 번호 확인
    private fun verifyUniversityAuth() {
        val universityDto = UniversityDto(
            token = binding.authUniversityNumEt.text.toString(),
            university = binding.searchUniversityView.query.toString(),
            updateDate = LocalDateTime.now()
        )

        val call: Call<UniversityDto> = ServiceCreator.service.verifyUniversityAuth(userId, universityDto)

        call.enqueue(object : Callback<UniversityDto> {
            override fun onResponse(call: Call<UniversityDto>, response: Response<UniversityDto>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AuthUniversityActivity, "인증 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    //Toast.makeText(this@AuthUniversityActivity, "인증 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UniversityDto>, t: Throwable) {
                Toast.makeText(this@AuthUniversityActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()

            }
        })
    }
}