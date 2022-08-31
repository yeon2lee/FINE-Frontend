package com.fine_app.ui.myPage

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fine_app.databinding.ActivityManageBookmarkBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.myPage.ManagePostAdapter
import com.fine_app.ui.myPage.ServiceCreator
import com.fine_app.ui.myPage.Post
import com.fine_app.ui.community.PostDetail_Group
import com.fine_app.ui.community.PostDetail_Main
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class ManageBookmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageBookmarkBinding
    lateinit var userInfo: SharedPreferences
    var userId by Delegates.notNull<Long>()
    lateinit var userData: List<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBookmarkBinding.inflate(layoutInflater)

        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)
        userId = userInfo.getString("userInfo", "2")!!.toLong()

        getMyBookmarkList()

        setContentView(binding.root)

        binding.mypageBeforeBtn2.setOnClickListener {
            finish()
        }
    }

    private fun getMyBookmarkList() {
        val call: Call<List<Post>> = ServiceCreator.service.getMyBookmarkList(userId)

        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    userData = response.body()!!
                } else {
                    userData = listOf()
                    Toast.makeText(this@ManageBookmarkActivity, "북마크 목록 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
                setAdapter()
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(this@ManageBookmarkActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun setAdapter(){
        val manageAdapter = ManagePostAdapter(userData)

        binding.bookmarkList.layoutManager = LinearLayoutManager(this)
        binding.bookmarkList.adapter = manageAdapter
        manageAdapter.setItemClickListener(object: ManagePostAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                var postDetail= Intent(this@ManageBookmarkActivity, PostDetail_Main::class.java)
                if (userData[position].group_check) {
                    postDetail= Intent(this@ManageBookmarkActivity, PostDetail_Group::class.java)
                }
                postDetail.putExtra("postingId", userData[position].postingId)
                postDetail.putExtra("memberId", userData[position].memberId)
                startActivity(postDetail)
            }
        })
    }

}