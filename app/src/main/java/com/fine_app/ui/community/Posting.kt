package com.fine_app.ui.community

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fine_app.GroupPosting
import com.fine_app.Post
import com.fine_app.Posting
import com.fine_app.R
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
        lateinit var title:String
        lateinit var content:String
        val memberID=1 as Long //todo 멤버아이디 불러오기
        var capacity=2
        val spinner: Spinner = binding.spinner
        val items = arrayOf("인원 선택", 2, 3, 4, 5, 6)
        var groupcheck=false
        val groupCheckList:RadioGroup=binding.groupCheck
        groupCheckList.setOnCheckedChangeListener{_, checkedId ->
            when(checkedId){
                R.id.radioButton_normal -> {
                    groupcheck=false
                    spinner.visibility=View.INVISIBLE
                }
                R.id.radioButton_group -> {
                    groupcheck=false
                    spinner.visibility=View.VISIBLE
                }
            }
        }
        spinner.adapter= ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> capacity=2
                    1 -> capacity=2
                    2 -> capacity=3
                    3 -> capacity=4
                    4 -> capacity=5
                    5 -> capacity=6
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
            if(groupcheck==false){
                val newPost=Posting(title, content, groupcheck)
                addMainPost(memberID, newPost)
            }else{
                val newPost= GroupPosting(title, content, groupcheck, capacity)
                addGroupPost(memberID, newPost)
            }
            finish()
        }
    }

    private fun addMainPost(memberID:Long?, postInfo:Posting){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= memberID ?:0
        val call = iRetrofit?.addMainPost(memberID = term, postInfo) ?:return

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
    private fun addGroupPost(memberID:Long?, postInfo:GroupPosting){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= memberID ?:0
        val call = iRetrofit?.addGroupPost(memberID = term, postInfo) ?:return

        call.enqueue(object : retrofit2.Callback<GroupPosting>{
            //응답성공
            override fun onResponse(call: Call<GroupPosting>, response: Response<GroupPosting>) {
                Log.d("retrofit", "글쓰기 - 응답 성공 / t : ${response.raw()}")
            }
            //응답실패
            override fun onFailure(call: Call<GroupPosting>, t: Throwable) {
                Log.d("retrofit", "글쓰기 - 응답 실패 / t: $t")
            }
        })
    }
}