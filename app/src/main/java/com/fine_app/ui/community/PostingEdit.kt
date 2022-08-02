package com.fine_app.ui.community

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.fine_app.EditPost
import com.fine_app.Post
import com.fine_app.databinding.CommunityPostingEditBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response


class PostingEdit : AppCompatActivity() {
    private lateinit var binding: CommunityPostingEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunityPostingEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lateinit var text:String

        val postTitle=intent.getStringExtra("title")
        val postContent=intent.getStringExtra("content")
        val postingId=intent.getLongExtra("postingId",0)

        binding.viewTitle.text=postTitle // 타이틀은 수정 불가
        binding.inputContents.setText(postContent) //기존 내용으로 텍스트 설정

        binding.inputContents.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                text=binding.inputContents.text.toString()
            }
        })
        binding.backButton.setOnClickListener{ //뒤로가기
            finish()
        }
        binding.finButton.setOnClickListener{ //등록
            editPosting(postingId, EditPost(text = text))
            finish()
        }
    }

    private fun editPosting(postingId:Long, text: EditPost){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.editPosting(postingId = postingId, text= text) ?:return
        call.enqueue(object : retrofit2.Callback<Post>{
            //응답성공
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "글 수정 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", "글 수정 : ${response.body().toString()}")
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "글 수정 - 응답 실패 / t: $t")
            }
        })
    }
}