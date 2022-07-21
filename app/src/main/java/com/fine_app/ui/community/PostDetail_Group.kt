package com.fine_app.ui.community

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.*
import com.fine_app.databinding.CommunityGroupPostBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostDetail_Group : AppCompatActivity() {
    private lateinit var binding: CommunityGroupPostBinding
    val writerID:Long=intent.getLongExtra("memberId", 0)
    val myID:Long=0 //todo 내 id 가져오기
    val postingID=intent.getLongExtra("postingId", 0)
    var mark=false
    var bookMarkId:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        binding = CommunityGroupPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //정보 받아옴
        val postTitle=intent.getStringExtra("title")
        val postContent=intent.getStringExtra("content")
        val postWriter=intent.getStringExtra("nickname")
        val postCapacity=intent.getIntExtra("capacity", 0)
        val createdDate=intent.getStringExtra("createdDate")
        val lastModifiedDate=intent.getStringExtra("lastModifiedDate")
        val closingCheck=intent.getBooleanExtra("closingCheck", false)
        val recruitingList= intent.getSerializableExtra("recruitingList") as ArrayList<Recruit>
        //val participants=intent.getIntExtra("participants", 0)//todo 현재 참여인원 받아오기
        //val postWriterImage=intent.getIntExtra("profileID", 0) //todo 사용자 이미지 표시

        val token=createdDate!!.split("-", "T", ":")
        val writtenTime=token[1]+"/"+token[2]+" "+token[3]+"/"+token[4]
        val modifiedWrittenTime= "$writtenTime (수정됨)"
        if(createdDate == lastModifiedDate) binding.writtenTime.text=writtenTime
        else binding.writtenTime.text=modifiedWrittenTime

        binding.postTitle.text=postTitle
        binding.postContent.text=postContent
        binding.writerName.text=postWriter
        binding.max.text=postCapacity.toString()
        //binding.participants.text=participants
        //binding.writerImage.setImageResource(postWriterImage)

        binding.backButton.setOnClickListener{ //글 세부페이지 종료
            finish()
        }
        binding.writerImage.setOnClickListener{ //작성자 프로필 조회
            val userProfile = Intent(this, ShowUserProfileActivity::class.java)
            userProfile.putExtra("memberId",writerID)
            startActivity(userProfile)
        }
        val editButton=binding.editButton //수정 페이지로 이동
        val deleteButton=binding.deleteButton
        val manageButton=binding.manageButton
        val joinButton=binding.joinButton
        val markButton=binding.markButton


        if (closingCheck==true){ //마감된 글이면 //todo 마감은 언제 되는데..?
            joinButton.visibility= INVISIBLE
        }else{
            joinButton.visibility= VISIBLE
            joinButton.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                    .setMessage("참여하시겠습니까?")
                    .setPositiveButton("확인",
                        DialogInterface.OnClickListener{ dialog, which ->
                            joinGroup(postingID,myID)
                            dialog.dismiss()
                            finish()
                        })
                    .setNegativeButton("취소",null)
                builder.show()
            }
        }

        if(writerID != myID){ //내가 쓴 글이 아니면 수정 삭제 관리 불가
            editButton.visibility= INVISIBLE
            deleteButton.visibility= INVISIBLE
            manageButton.visibility= INVISIBLE
        }else{
            markButton.visibility= INVISIBLE //내가 쓴 글이면 북마크 버튼 안 보임
            joinButton.visibility= INVISIBLE //내가 쓴 글이면 참여버튼 안 보임
            editButton.setOnClickListener{
                val postDetail= Intent(this, PostingEdit::class.java)
                postDetail.putExtra("postingID", postingID)
                postDetail.putExtra("title", postTitle)
                postDetail.putExtra("content", postContent)
                startActivity(postDetail)
                finish()
            }
            deleteButton.setOnClickListener{
                val builder = AlertDialog.Builder(this)
                    .setMessage("글을 삭제하시겠습니까?")
                    .setPositiveButton("삭제",
                        DialogInterface.OnClickListener{ dialog, which ->
                            deletePosting(postingID)
                            dialog.dismiss()
                            finish()
                        })
                    .setNegativeButton("취소",null)
                builder.show()
            }
            manageButton.setOnClickListener{
                val waitingList = Intent(this, WaitingList::class.java)
                waitingList.putExtra("postingID", postingID)
                waitingList.putExtra("recruitingList",recruitingList )
                startActivity(waitingList)
                finish()
            }
            markButton.setOnClickListener{
                if (mark){//북마크 취소
                    val builder = AlertDialog.Builder(this)
                        .setMessage("북마크를 취소하였습니다.")
                    builder.show()
                    deleteBookMark(bookMarkId)
                    markButton.text="북마크"
                    mark=false
                } else{//북마크 추가
                    val builder = AlertDialog.Builder(this)
                        .setMessage("북마크를 추가했습니다.")
                    builder.show()
                    val newBookMark=BookMark(myID, postingID, bookMarkId)
                    addBookMark(newBookMark)
                    markButton.text="저장됨"
                    mark=true
                }
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
            val newComment=Comment(myID, postingID, text, 0) //todo commentId 처리 어떻게?
            addComment(newComment)
        }

    }
    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var comment: Comment
        private val nickname: TextView =itemView.findViewById(R.id.nickname)
        private val text: TextView =itemView.findViewById(R.id.comment)
        private val image: ImageView =itemView.findViewById(R.id.profileImage)
        fun bind(comment:Comment){
            this.comment=comment
            nickname.text=this.comment.memberId.toString()
            text.text=this.comment.text
            //image.setImageResource(this.comment.profileID)

            image.setOnClickListener{ //댓글 작성자 프로필 조회
                val userProfile = Intent(this@PostDetail_Group, ShowUserProfileActivity::class.java)
                userProfile.putExtra("memberId",this.comment.memberId)
                startActivity(userProfile)
            }
            itemView.setOnLongClickListener{
                val items=arrayOf("수정", "삭제")
                val builder = AlertDialog.Builder(this@PostDetail_Group)
                builder.setTitle("댓글 관리")
                builder.setItems(items){ dialog, which ->
                    if(which == 0) {
                        dialog.dismiss()
                        binding.putComment.setText(this.comment.text) //수정 전 댓글을 보여줌
                        var text=""
                        binding.putComment.addTextChangedListener(object: TextWatcher {
                            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                            override fun afterTextChanged(p0: Editable?) {
                                text=binding.putComment.text.toString()
                            }
                        })
                        val newComment=Comment(myID, postingID, text, this.comment.commentId)
                        editComment(this.comment.commentId, newComment)
                    }
                    else {
                        dialog.dismiss()
                        deleteComment(this.comment.commentId)
                    }
                }
                    .show()
                true
            }
        }

    }
    inner class MyAdapter(var list:List<Comment>): RecyclerView.Adapter<MyViewHolder>(){
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

    //---------------------------------API 연결-----------------------------------------------------

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
    private fun joinGroup(PostingID:Long?, memberId:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= PostingID ?:0
        val call = iRetrofit?.joinGroup(postingId = term, memberId=memberId, false) ?:return

        call.enqueue(object : Callback<Join>{
            //응답성공
            override fun onResponse(call: Call<Join>, response: Response<Join>) {
                Log.d("retrofit", "참여하기 - 응답 성공 / t : ${response.raw()}")
            }
            //응답실패
            override fun onFailure(call: Call<Join>, t: Throwable) {
                Log.d("retrofit", "참여하기 - 응답 실패 / t: $t")
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
                bookMarkId=response.body()!!.BookmarkId
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
    private fun editComment(commentId: Long?, comment: Comment){
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
}