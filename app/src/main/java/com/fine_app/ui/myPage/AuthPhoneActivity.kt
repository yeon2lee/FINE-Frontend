package com.fine_app.ui.myPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import com.fine_app.R
import com.fine_app.databinding.ActivityAuthPhoneBinding
import com.fine_app.ui.MyPage.Post
import com.fine_app.ui.MyPage.Profile
import com.fine_app.ui.MyPage.RequestProfileData
import com.fine_app.ui.MyPage.ServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Spinner

class AuthPhoneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthPhoneBinding
    var phoneNumber = "01012345678"
    override fun onCreate(savedInstanceState: Bundle?) {
        val spinner: Spinner = binding.spinner3
        val items = arrayOf("010", "011", "017", "02")

        super.onCreate(savedInstanceState)
        binding = ActivityAuthPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spinner.adapter= ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> phoneNumber = "010"
                    1 -> phoneNumber = "010"
                    2 -> phoneNumber = "011"
                    3 -> phoneNumber = "017"
                    4 -> phoneNumber = "02"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // todo 전화번호로 인증 번호 전송
    private fun sendAuthMesssage() {
        val userId = 1.toLong()
        phoneNumber.plus(binding.authPhoneCertEt.text.toString())

        val call: Call<Long> = ServiceCreator.service.sendAuthMesssage(userId, phoneNumber)

        call.enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful) {
                    //Toast.makeText(this@AuthPhoneActivity, "인증번호 전송 성공", Toast.LENGTH_SHORT).show()
                } else {
                    //Toast.makeText(this@AuthPhoneActivity, "인증번호 전송 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Toast.makeText(this@AuthPhoneActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()

            }
        })

    }

    // todo 인증 번호 확인
    private fun verifyAuth() {
        val userId = 1.toLong()
        val token = binding.authPhoneCertEt.text.toString()
        val call: Call<Long> = ServiceCreator.service.verifyAuth(userId, token)

        call.enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful) {
                    //Toast.makeText(this@AuthPhoneActivity, "인증 성공", Toast.LENGTH_SHORT).show()

                } else {
                    //Toast.makeText(this@AuthPhoneActivity, "인증 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Toast.makeText(this@AuthPhoneActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()

            }
        })
    }
}