package com.fine_app.ui.community

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Comment
import com.fine_app.R
import com.fine_app.databinding.CommunityMainPostBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response


class PostDetail_Main : AppCompatActivity() {
    private lateinit var binding: CommunityMainPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunityMainPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //정보 받아옴
        val postTitle=intent.getStringExtra("title")
        val postContent=intent.getStringExtra("content")
        val postWriter=intent.getStringExtra("nickname")
        val postingID=intent.getLongExtra("postingID", 0)
        //val postWriterImage=intent.getIntExtra("profileID", 0) //todo 사용자 이미지 표시

        //레이아웃에 부착
        val title: TextView =binding.postTitle
        val content: TextView =binding.postContent
        val nickname:TextView=binding.writerName
        //val image:ImageView=binding.writerImage
        title.text=postTitle
        content.text=postContent
        nickname.text=postWriter
        //image.setImageResource(postWriterImage)

        binding.backButton.setOnClickListener{ //글 세부페이지 종료
            finish()
        }
        binding.writerImage.setOnClickListener{
            val userProfile = Intent(this, ShowUserProfileActivity::class.java)
            startActivity(userProfile)
        }

        val editButton=binding.editButton //수정 페이지로 이동
        val deleteButton=binding.deleteButton
        val writerID:Long=0 //todo 글 작성자 id 가져오기
        val myID:Long=0 //todo 내 id 가져오기

        if(writerID != myID){ //내가 쓴 글이 아니면 수정 삭제 불가
            editButton.visibility=View.INVISIBLE
            deleteButton.visibility=View.INVISIBLE
        }else{
            editButton.setOnClickListener{
                val postDetail= Intent(this, PostingEdit::class.java)
                postDetail.putExtra("postingID", postingID) //todo 포스팅아이디 확인
                postDetail.putExtra("title", postTitle)
                postDetail.putExtra("content", postContent)
                startActivity(postDetail)
            }
            deleteButton.setOnClickListener{
                val builder = AlertDialog.Builder(this)
                    .setMessage("글을 삭제하시겠습니까?")
                    .setPositiveButton("삭제",
                        DialogInterface.OnClickListener{ dialog, which ->
                            deletePosting(postingID)
                            finish()
                        })
                    .setNegativeButton("취소",null)
                builder.show()
            }
        }

        //-----------------------------댓글------------------------------------------------------
        val recyclerView:RecyclerView=binding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(this)
        val comments= intent.getSerializableExtra("comments") as ArrayList<Comment>
        val adapter=MyAdapter(comments)
        recyclerView.adapter=adapter

        val commentButton=binding.commentButton
        commentButton.setOnClickListener{
            var text=""
            binding.putComment.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    text=binding.putComment.text.toString()
                }
            })
            val newComment=Comment(myID, postingID, text, 0) //todo commentid 처리 어떻게?
            addComment(newComment)
        }

        // 사용자 프로필 조회
        binding.writerImage.setOnClickListener{
            val userProfile = Intent(this, ShowUserProfileActivity::class.java)
            startActivity(userProfile)
        }
    }
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var comment: Comment
        private val nickname: TextView =itemView.findViewById(R.id.nickname)
        private val text: TextView =itemView.findViewById(R.id.comment)
        private val image:ImageView=itemView.findViewById(R.id.profileImage)
        fun bind(comment:Comment){
            this.comment=comment
            nickname.text=this.comment.memberId.toString() //todo 닉네임이 멤버 아이디 맞나..?
            text.text=this.comment.text
            //image.setImageResource(this.comment.profileID)
        }

    }

    inner class MyAdapter(var list:ArrayList<Comment>): RecyclerView.Adapter<MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val inflater=LayoutInflater.from(parent.context)
            val view=inflater.inflate(R.layout.item_comment, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val comment=list[position]
            holder.bind(comment)
        }
        override fun getItemCount()=list.size
    }

//------------------------------------API 연결-------------------------------------------

    private fun deletePosting(PostingID:Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= PostingID ?:0
        val call = iRetrofit?.deletePosting(PostingID = term) ?:return

        call.enqueue(object : retrofit2.Callback<Long>{
            //응답성공
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("retrofit", "글 삭제 - 응답 성공 / t : ${response.raw()}")
            }
            //응답실패
            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit", "글 삭제 - 응답 실패 / t: $t")
            }
        })
    }

    private fun addComment(comment: Comment){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.addComment(comment) ?:return

        call.enqueue(object : retrofit2.Callback<Comment>{
            //응답성공
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                Log.d("retrofit", "댓글 추가 - 응답 성공 / t : ${response.raw()}")
            }
            //응답실패
            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Log.d("retrofit", "댓글 추가 - 응답 실패 / t: $t")
            }
        })
    }
}