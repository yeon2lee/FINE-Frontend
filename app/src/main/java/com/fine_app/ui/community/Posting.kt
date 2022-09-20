package com.fine_app.ui.community

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.fine_app.Post
import com.fine_app.Posting
import com.fine_app.R
import com.fine_app.databinding.CommunityPostingBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response
import kotlin.properties.Delegates


class Posting : AppCompatActivity(), ConfirmDialogInterface {
    private lateinit var binding: CommunityPostingBinding
    private var myID by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences
    lateinit var keyWord:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CommunityPostingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)
        myID = userInfo.getString("userInfo", "2")!!.toLong()

        lateinit var title:String
        lateinit var content:String
        var capacity=2
        val spinner: Spinner = binding.spinner
        val items = arrayOf("인원 선택", 2, 3, 4, 5, 6)
        val spinner2: Spinner = binding.spinner2
        val spinner2Item = arrayOf("자유","기획, 전략","회계, 재무", "유통, 물류", "연구개발, 설계",  "건축, 인테리어","의료, 보건","미디어",
            "영업, 영업관리", "마케팅, 광고, 홍보","인사, 노무" ,"IT, SW","생산, 생산관리", "토목, 환경", "교육", "디자인"  )
        var groupCheck=false
        val groupCheckList: RadioGroup =binding.groupCheck
        groupCheckList.setOnCheckedChangeListener{_, checkedId ->
            when(checkedId){
                R.id.radioButton_normal -> {
                    groupCheck=false
                    spinner.visibility=View.INVISIBLE
                }
                R.id.radioButton_group -> {
                    groupCheck=true
                    spinner.visibility= View.VISIBLE
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
        spinner2.adapter= ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinner2Item)
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> keyWord="자유"
                    1 -> keyWord="기획, 전략"
                    2 -> keyWord="회계, 재무"
                    3 -> keyWord="유통, 물류"
                    4 -> keyWord="연구개발, 설계"
                    5 -> keyWord="건축, 인테리어"
                    6-> keyWord="의료, 보건"
                    7-> keyWord="미디어"
                    8-> keyWord="영업, 영업관리"
                    9-> keyWord="마케팅, 광고"
                    10-> keyWord="인사, 노무"
                    11-> keyWord="IT, SW"
                    12-> keyWord="생산, 생산관리"
                    13-> keyWord="토목, 환경"
                    14-> keyWord="교육"
                    15-> keyWord="디자인"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.inputTitle.addTextChangedListener(object: TextWatcher {
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
            val dialog = ConfirmDialog(this, "글쓰기를 취소하시겠습니까?", 0, 0)
            dialog.isCancelable = false
            dialog.show(this.supportFragmentManager, "ConfirmDialog")
        }
        binding.finButton.setOnClickListener{ //등록
            val newPost= Posting(title, content, groupCheck, capacity, keyWord)
            addPost(myID, newPost)
            finish()
        }

    }

    override fun onYesButtonClick(num: Int, theme:Int) {
        finish()
    }

    private fun addPost(memberId:Long?, postInfo:Posting){ //todo 포스팅에서 키워드 넘겨주기
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= memberId ?:0
        val call = iRetrofit?.addPost(memberId = term, postInfo) ?:return

        call.enqueue(object : retrofit2.Callback<Post>{

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "글쓰기 - 응답 성공 / t : ${response.raw()}")
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "글쓰기 - 응답 실패 / t: $t")
            }
        })
    }
}