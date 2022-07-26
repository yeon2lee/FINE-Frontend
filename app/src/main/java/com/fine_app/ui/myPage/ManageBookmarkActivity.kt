package com.fine_app.ui.myPage

import android.content.Intent
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
import com.fine_app.ui.MyPage.ManagePostAdapter
import com.fine_app.ui.MyPage.Post
import com.fine_app.ui.MyPage.ServiceCreator
import com.fine_app.ui.community.PostDetail_Main
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageBookmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageBookmarkBinding
    var userId: Long = 0
    lateinit var userData: List<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBookmarkBinding.inflate(layoutInflater)

        getMyBookmarkList()

        setContentView(binding.root)

        binding.mypageBeforeBtn2.setOnClickListener {
            finish()
        }
    }

    private fun getMyBookmarkList() {
        userId = 1

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
    private fun viewMainPosting(postingId:Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= postingId ?:0
        val call = iRetrofit?.viewMainPosting(postingId = term) ?:return

        call.enqueue(object : retrofit2.Callback<com.fine_app.Post>{

            override fun onResponse(call: Call<com.fine_app.Post>, response: Response<com.fine_app.Post>) {
                Log.d("retrofit", "메인 커뮤니티 세부 글 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", response.body().toString())

                val postDetail= Intent(this@ManageBookmarkActivity, PostDetail_Main::class.java)
                postDetail.putExtra("nickname", response.body()!!.nickname)
                postDetail.putExtra("title", response.body()!!.title)
                postDetail.putExtra("content", response.body()!!.content)
                postDetail.putExtra("comments", response.body()!!.comments)
                postDetail.putExtra("capacity", response.body()!!.capacity)
                postDetail.putExtra("createdDate", response.body()!!.createdDate)
                postDetail.putExtra("lastModifiedDate", response.body()!!.lastModifiedDate)
                postDetail.putExtra("memberId", response.body()!!.memberId)
                postDetail.putExtra("postingId", postingId)
                startActivity(postDetail)
            }

            //응답실패
            override fun onFailure(call: Call<com.fine_app.Post>, t: Throwable) {
                Log.d("retrofit", "메인 커뮤니티 세부 글 - 응답 실패 / t: $t")
            }
        })
    }

    private fun setAdapter(){
        val manageAdapter = ManagePostAdapter(userData)

        binding.bookmarkList.layoutManager = LinearLayoutManager(this)
        binding.bookmarkList.adapter = manageAdapter
        manageAdapter.setItemClickListener(object: ManagePostAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                viewMainPosting(position.toLong())
            }
        })
    }

}