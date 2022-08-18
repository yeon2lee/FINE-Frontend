package com.fine_app.ui.myPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fine_app.databinding.ActivityManageBookmarkBinding
import com.fine_app.databinding.ActivityManageGroupBinding
import com.fine_app.ui.myPage.ManagePostAdapter
import com.fine_app.ui.myPage.Post
import com.fine_app.ui.myPage.ServiceCreator
import com.fine_app.ui.community.PostDetail_Group
import com.fine_app.ui.community.PostDetail_Main
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageGroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageGroupBinding
    var userId: Long = 0
    lateinit var userData: List<Post>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageGroupBinding.inflate(layoutInflater)
        getMyGroupList()
        setContentView(binding.root)
        binding.mypageBeforeBtn4.setOnClickListener {
            finish()
        }
    }

    private fun getMyGroupList() {
        userId = 1

        val call: Call<List<Post>> = ServiceCreator.service.getMyGroupList(userId)

        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    userData = response.body()!!
                } else {
                    userData = listOf()
                    Toast.makeText(this@ManageGroupActivity, "그룹 신청글 목록 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
                setAdapter()
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(this@ManageGroupActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setAdapter(){
        val manageAdapter = ManagePostAdapter(userData)

        binding.groupList.layoutManager = LinearLayoutManager(this)
        binding.groupList.adapter = manageAdapter
        manageAdapter.setItemClickListener(object: ManagePostAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val postDetail= Intent(this@ManageGroupActivity, PostDetail_Group::class.java)
                postDetail.putExtra("postingId", userData[position].postingId)
                postDetail.putExtra("memberId", userData[position].memberId)
                startActivity(postDetail)
            }
        })
    }
}