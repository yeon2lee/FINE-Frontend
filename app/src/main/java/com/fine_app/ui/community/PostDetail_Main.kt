package com.fine_app.ui.community

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.BookMark
import com.fine_app.Comment
import com.fine_app.NewComment
import com.fine_app.R
import com.fine_app.databinding.CommunityMainPostBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response
import kotlin.properties.Delegates


class PostDetail_Main : AppCompatActivity(), ConfirmDialogInterface {
    private lateinit var binding: CommunityMainPostBinding
    private var postingId by Delegates.notNull<Long>()
    private var writerID by Delegates.notNull<Long>()
    private var myID by Delegates.notNull<Long>()
    private val comments= intent?.getSerializableExtra("comments") as ArrayList<Comment>
    val adapter=MyAdapter(comments)
    private var mark=false
    var bookMarkId:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding = CommunityMainPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postingId=intent.getLongExtra("postingId", 1)
        writerID=intent.getLongExtra("memberId", 1)
        myID=1 //todo 내 id 가져오기

        //정보 받아옴
        val postTitle=intent.getStringExtra("title")
        val postContent=intent.getStringExtra("content")
        val postWriter=intent.getStringExtra("nickname")
        val createdDate=intent.getStringExtra("createdDate")
        val lastModifiedDate=intent.getStringExtra("lastModifiedDate")
        //val postWriterImage=intent.getIntExtra("profileID", 0) //todo 사용자 이미지 표시

        val token=createdDate!!.split("-", "T", ":")
        val writtenTime=token[1]+"/"+token[2]+" "+token[3]+"/"+token[4]
        val modifiedWrittenTime= "$writtenTime (수정됨)"

        if(createdDate == lastModifiedDate) binding.writtenTime.text=writtenTime
        else binding.writtenTime.text=modifiedWrittenTime

        binding.postTitle.text=postTitle
        binding.postContent.text=postContent
        binding.writerName.text=postWriter
        //binding.writerImage.setImageResource(postWriterImage)

        binding.writerImage.setOnClickListener{ //작성자 프로필 조회
            val userProfile = Intent(this, ShowUserProfileActivity::class.java)
            userProfile.putExtra("memberId",writerID)
            startActivity(userProfile)
        }
        //-----------------------------------------------------버튼 클릭-------------------------------------------------
        binding.backButton.setOnClickListener{ //글 세부페이지 종료
            finish()
        }

        val editButton=binding.editButton
        val deleteButton=binding.deleteButton
        val markButton=binding.markButton

        if(writerID != myID){ //내가 쓴 글이 아니면 수정 삭제 불가
            editButton.visibility= INVISIBLE
            deleteButton.visibility= INVISIBLE
        }else{
            markButton.visibility= INVISIBLE //내가 쓴 글이면 북마크 버튼 안 보임
            editButton.setOnClickListener{
                val postDetail= Intent(this, PostingEdit::class.java)
                postDetail.putExtra("postingId", postingId)
                postDetail.putExtra("title", postTitle)
                postDetail.putExtra("content", postContent)
                startActivity(postDetail)
                finish()
            }
            deleteButton.setOnClickListener{
                val dialog = ConfirmDialog(this, "글을 삭제하시겠습니까?", 0,0)
                dialog.isCancelable = false
                dialog.show(this.supportFragmentManager, "ConfirmDialog")
            }
            markButton.setOnClickListener{
                if (mark){//북마크 취소
                    val dialog = ConfirmDialog(this, "북마크를 취소하였습니다.", 1,1)
                    dialog.isCancelable = false
                    dialog.show(this.supportFragmentManager, "ConfirmDialog")
                    deleteBookMark(bookMarkId)
                    markButton.text="북마크"
                    mark=false
                } else{//북마크 추가
                    val dialog = ConfirmDialog(this, "북마크를 추가했습니다.", 2,1)
                    dialog.isCancelable = false
                    dialog.show(this.supportFragmentManager, "ConfirmDialog")
                    val newBookMark=BookMark(myID, postingId, bookMarkId)
                    addBookMark(newBookMark)
                    markButton.text="저장됨"
                    mark=true
                }
            }
        }

        //-----------------------------댓글------------------------------------------------------
        val recyclerView:RecyclerView=binding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(this)
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
            val newComment= NewComment(myID, postingId, text)
            addComment(newComment)
            adapter.notifyDataSetChanged()
        }

    }
    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var comment: Comment
        private val nickname: TextView =itemView.findViewById(R.id.nickname)
        private val text: TextView =itemView.findViewById(R.id.comment)
        private val image:ImageView=itemView.findViewById(R.id.profileImage)
        fun bind(comment:Comment){
            this.comment=comment
            nickname.text=this.comment.member.nickname
            text.text=this.comment.text
            //image.setImageResource(this.comment.profileID)

            image.setOnClickListener{ //댓글 작성자 프로필 조회
                val userProfile = Intent(this@PostDetail_Main, ShowUserProfileActivity::class.java)
                userProfile.putExtra("memberId",this.comment.member.memberId)
                startActivity(userProfile)
            }

            itemView.setOnLongClickListener{
                val items=arrayOf("수정", "삭제")
                val builder = AlertDialog.Builder(this@PostDetail_Main)
                builder.setTitle("댓글 관리")
                builder.setItems(items){ _, which ->
                    if(which == 0) {
                        binding.putComment.setText(this.comment.text) //수정 전 댓글을 보여줌
                        var text=""
                        binding.putComment.addTextChangedListener(object: TextWatcher {
                            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                            override fun afterTextChanged(p0: Editable?) {
                                text=binding.putComment.text.toString()
                            }
                        })
                        val newComment=NewComment(myID, postingId, text)
                        editComment(this.comment.commentId, newComment)
                    }
                    else deleteComment(this.comment.commentId)
                    adapter.notifyDataSetChanged()
                }
                    .show()
                true
            }
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
    override fun onYesButtonClick(num: Int, theme:Int) {
        deletePosting(postingId)
    }
//------------------------------------API 연결-------------------------------------------

    private fun deletePosting(PostingID:Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= PostingID ?:0
        val call = iRetrofit?.deletePosting(postingId = term) ?:return

        call.enqueue(object : retrofit2.Callback<Long>{
            //응답성공
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("retrofit", "글 삭제 - 응답 성공 / t : ${response.raw()}")
                finish()
            }
            //응답실패
            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit", "글 삭제 - 응답 실패 / t: $t")
            }
        })
    }

    private fun addComment(comment: NewComment){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.addComment(comment) ?:return

        call.enqueue(object : retrofit2.Callback<NewComment>{
            //응답성공
            override fun onResponse(call: Call<NewComment>, response: Response<NewComment>) {
                Log.d("retrofit", "댓글 추가 - 응답 성공 / t : ${response.raw()}")
            }
            //응답실패
            override fun onFailure(call: Call<NewComment>, t: Throwable) {
                Log.d("retrofit", "댓글 추가 - 응답 실패 / t: $t")
            }
        })
    }
    private fun editComment(commentId: Long?, comment: NewComment){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= commentId ?:0
        val call = iRetrofit?.editComment(commentId=term, comment) ?:return

        call.enqueue(object : retrofit2.Callback<Comment>{
            //응답성공
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                Log.d("retrofit", "댓글 수정 - 응답 성공 / t : ${response.raw()}")
            }
            //응답실패
            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Log.d("retrofit", "댓글 수정 - 응답 실패 / t: $t")
            }
        })
    }
    private fun deleteComment(commentId: Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= commentId ?:0
        val call = iRetrofit?.deleteComment(commentId=term) ?:return

        call.enqueue(object : retrofit2.Callback<Long>{
            //응답성공
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("retrofit", "댓글 삭제 - 응답 성공 / t : ${response.raw()}")
            }
            //응답실패
            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit", "댓글 삭제 - 응답 실패 / t: $t")
            }
        })
    }
    private fun addBookMark(BookMark: BookMark){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.addBookMark(BookMark) ?:return

        call.enqueue(object : retrofit2.Callback<BookMark>{
            //응답성공
            override fun onResponse(call: Call<BookMark>, response: Response<BookMark>) {
                Log.d("retrofit", "북마크 추가 - 응답 성공 / t : ${response.raw()}")
                bookMarkId=response.body()!!.bookmarkId
            }
            //응답실패
            override fun onFailure(call: Call<BookMark>, t: Throwable) {
                Log.d("retrofit", "북마크 추가 - 응답 실패 / t: $t")
            }
        })
    }
    private fun deleteBookMark(bookMarkId: Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= bookMarkId ?:0
        val call = iRetrofit?.deleteBookMark(bookMarkId=term) ?:return

        call.enqueue(object : retrofit2.Callback<Long>{
            //응답성공
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("retrofit", "북마크 삭제 - 응답 성공 / t : ${response.raw()}")
            }
            //응답실패
            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit", "북마크 삭제 - 응답 실패 / t: $t")
            }
        })
    }
}