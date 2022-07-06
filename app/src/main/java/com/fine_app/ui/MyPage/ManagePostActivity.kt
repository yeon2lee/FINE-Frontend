package com.fine_app.ui.MyPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fine_app.databinding.ActivityMypagePostBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypagePostBinding
    var userId: Long = 0
    var userData: List<Post> = listOf(Post("user", 1, "title", "content", "comment", "", ""))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMypagePostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getMyPostList()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ManagePostAdapter(userData)
    }

    private fun getMyPostList() {
        userId = 1

        val call: Call<List<Post>> = ServiceCreator.service.getMyPostList(userId)

        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    userData = response.body()!!

                    Toast.makeText(this@ManagePostActivity,"성공", Toast.LENGTH_SHORT).show()
                    val data = response.body()?.get(0)?.nickname
                    Toast.makeText(this@ManagePostActivity, "${data}님 반갑습니다!", Toast.LENGTH_SHORT).show()
                } else {

                    Toast.makeText(this@ManagePostActivity, "내가 쓴 글 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(this@ManagePostActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }

        })

    }
}

