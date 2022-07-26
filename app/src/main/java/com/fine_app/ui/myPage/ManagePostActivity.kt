package com.fine_app.ui.MyPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fine_app.databinding.ActivityMypagePostBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.community.PostDetail_Main
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypagePostBinding
    var userId: Long = 0
    lateinit var userData: List<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMypagePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getMyPostList()

        binding.mypageBeforeBtn.setOnClickListener {
            finish()
        }

    }

    private fun getMyPostList() {
        userId = 1

        val call: Call<List<Post>> = ServiceCreator.service.getMyPostList(userId)

        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    userData = response.body()!!
                } else {
                    userData = listOf()
                    Toast.makeText(this@ManagePostActivity, "내가 쓴 글 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
                setAdapter()
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(this@ManagePostActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
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

                val postDetail= Intent(this@ManagePostActivity, PostDetail_Main::class.java)
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

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = manageAdapter
        manageAdapter.setItemClickListener(object: ManagePostAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                viewMainPosting(position.toLong())
            }
        })
    }

}

