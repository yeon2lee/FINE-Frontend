package com.fine_app.ui.community

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fine_app.Post
import com.fine_app.Posting
import com.fine_app.databinding.CommunityPostingBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response


class Posting : AppCompatActivity() {
    private lateinit var binding: CommunityPostingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunityPostingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //val title=binding.inputTitle.text.toString()
        //val content:String =binding.inputContents.text.toString()
        lateinit var title:String
        lateinit var content:String
        val spinner: Spinner = binding.spinner
        val items = arrayOf("인원 선택", 1, 2, 3, 4, 5, 6)
        var capacity="1"
        var groupcheck=false

        spinner.adapter= ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> capacity="1"
                    1 -> capacity="1"
                    2 -> {capacity="2"
                    groupcheck=true}
                    3 -> {capacity="3"
                        groupcheck=true}
                    4 -> {capacity="4"
                        groupcheck=true}
                    5 -> {capacity="5"
                        groupcheck=true}
                    6 -> {capacity="6"
                        groupcheck=true
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}

        }
        binding.inputTitle.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                title=binding.inputTitle.text.toString()
            }
        })
        binding.inputContents.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                content=binding.inputContents.text.toString()
            }
        })

        binding.backButton.setOnClickListener{ //뒤로가기
            finish()
        }
        binding.finButton.setOnClickListener{ //등록
            val newpost=Posting(title, content, groupcheck)
            addMainpost("1", newpost)
            finish()
            //CommunityGroupFragment().onResume()
        }
    }


    private fun addMainpost(memberID:String?, postInfo:Posting){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:String= memberID ?:""
        val call = iRetrofit?.addMainpost(memberID = term, postInfo) ?:return

        call.enqueue(object : retrofit2.Callback<Post>{
            //응답성공
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "글쓰기 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", "id:  ${memberID}")
                Log.d("retrofit", "id:  ${postInfo.title}")
                Log.d("retrofit", "id:  ${postInfo.content}")
                Log.d("retrofit", "id:  ${postInfo.groupCheck}")
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "글쓰기 - 응답 실패 / t: $t")
            }

        })
    }
}