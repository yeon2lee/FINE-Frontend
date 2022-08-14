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
import com.fine_app.*
import com.fine_app.databinding.CommunityGroupPostBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class PostDetail_Group : AppCompatActivity(), ConfirmDialogInterface {
    private lateinit var binding: CommunityGroupPostBinding
    private lateinit var adapter: MyAdapter
    private var postingID by Delegates.notNull<Long>()
    private var writerID by Delegates.notNull<Long>()
    private val myID:Long=2//todo 내 id 가져오기
    private var postTitle by Delegates.notNull<String>()
    private var postContent by Delegates.notNull<String>()
    private var postWriter by Delegates.notNull<String>()
    private var createdDate by Delegates.notNull<String>()
    private var lastModifiedDate by Delegates.notNull<String>()
    private var comments by Delegates.notNull<ArrayList<Comment>>()
    private var postCapacity by Delegates.notNull<Int>()
    private var closingCheck by Delegates.notNull<Boolean>()
    private var recruitingList by Delegates.notNull<ArrayList<Recruit>>()
    private var participants by Delegates.notNull<Int>()
    private var bookMarkId:Long=0//todo 북마크 아이디
    private var recruitingId:Long=0//todo 참가하기 아이디

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        binding = CommunityGroupPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postingID=intent.getLongExtra("postingId", 1)
        viewPosting(postingID, myID)

    }
    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var comment: Comment
        private val nickname: TextView =itemView.findViewById(R.id.nickname)
        private val text: TextView =itemView.findViewById(R.id.comment)
        private val image: ImageView =itemView.findViewById(R.id.profileImage)
        fun bind(comment:Comment){
            this.comment=comment
            nickname.text=this.comment.member.nickname
            text.text=this.comment.text
            //image.setImageResource(this.comment.profileID)

            image.setOnClickListener{ //댓글 작성자 프로필 조회
                val userProfile = Intent(this@PostDetail_Group, ShowUserProfileActivity::class.java)
                userProfile.putExtra("memberId",this.comment.member.memberId)
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
                        val newComment=NewComment(myID, postingID, text)
                        editComment(this.comment.commentId,newComment )
                        binding.putComment.setText("")
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
    override fun onYesButtonClick(num: Int, theme:Int) {
        when (num) {
            0 -> joinGroup(postingID,myID, AcceptCheck(false))
            1 -> deletePosting(postingID)
            4 -> cancelJoinGroup(recruitingId)
        }
        finish()
    }

    //---------------------------------API 연결-----------------------------------------------------
    private fun attach(){
        postingID=intent.getLongExtra("postingId", 1)

        val token= createdDate.split("-", "T", ":")
        val writtenTime=token[1]+"/"+token[2]+" "+token[3]+":"+token[4]
        val modifiedWrittenTime= "$writtenTime (수정됨)"

        if(createdDate == lastModifiedDate) binding.writtenTime.text=writtenTime
        else binding.writtenTime.text=modifiedWrittenTime

        binding.postTitle.text=postTitle
        binding.postContent.text=postContent
        binding.writerName.text=postWriter
        binding.max.text=postCapacity.toString()
        binding.participants.text=participants.toString()
        //binding.writerImage.setImageResource(postWriterImage)

        binding.backButton.setOnClickListener{ //글 세부페이지 종료
            finish()
        }
        binding.writerImage.setOnClickListener{ //작성자 프로필 조회
            val userProfile = Intent(this, ShowUserProfileActivity::class.java)
            userProfile.putExtra("memberId",writerID)
            startActivity(userProfile)
        }
        val editButton=binding.editButton
        val deleteButton=binding.deleteButton
        val manageButton=binding.manageButton
        val joinButton=binding.joinButton
        val markButton=binding.markButton

        if (closingCheck){
            joinButton.visibility= INVISIBLE
        }else{
            joinButton.visibility= VISIBLE
            joinButton.setOnClickListener {
                if (recruitingId!=0.toLong()){//참여하기 취소
                    val dialog = ConfirmDialog(this, "참여를 취소하시겠습니까?", 4, 0)
                    dialog.isCancelable = false
                    dialog.show(this.supportFragmentManager, "ConfirmDialog")

                } else{//참여하기 신청
                    val dialog = ConfirmDialog(this, "참여하시겠습니까?", 0, 0)
                    dialog.isCancelable = false
                    dialog.show(this.supportFragmentManager, "ConfirmDialog")
                }
            }
        }

        if(writerID != myID){ //내가 쓴 글이 아니면 수정 삭제 관리 불가
            editButton.visibility= INVISIBLE
            deleteButton.visibility= INVISIBLE
            manageButton.visibility= INVISIBLE
            markButton.setOnClickListener{
                if (bookMarkId!=0.toLong()){//북마크 취소
                    val dialog = ConfirmDialog(this, "북마크를 취소하였습니다.", 2,1)
                    dialog.show(this.supportFragmentManager, "ConfirmDialog")
                    deleteBookMark(bookMarkId)

                } else{//북마크 추가
                    val dialog = ConfirmDialog(this, "북마크를 추가했습니다.", 3,1)
                    dialog.show(this.supportFragmentManager, "ConfirmDialog")
                    val newBookMark=BookMark(myID, postingID)
                    addBookMark(newBookMark)
                }
            }
        }else{
            markButton.visibility= INVISIBLE //내가 쓴 글이면 북마크 버튼 안 보임
            joinButton.visibility= INVISIBLE //내가 쓴 글이면 참여버튼 안 보임
            editButton.setOnClickListener{
                val postDetail= Intent(this, PostingEdit::class.java)
                postDetail.putExtra("postingId", postingID)
                postDetail.putExtra("title", postTitle)
                postDetail.putExtra("content", postContent)
                startActivity(postDetail)
                onResume()
            }
            deleteButton.setOnClickListener{
                val dialog = ConfirmDialog(this, "글을 삭제하시겠습니까?", 1,0)
                dialog.isCancelable = false
                dialog.show(this.supportFragmentManager, "ConfirmDialog")
            }
            manageButton.setOnClickListener{
                val waitingList = Intent(this, WaitingList::class.java)
                waitingList.putExtra("postingID", postingID)
                waitingList.putExtra("myID", myID)
                //waitingList.putExtra("recruitingList",recruitingList)
                //waitingList.putExtra("closingCheck",closingCheck )
                startActivity(waitingList)
                onResume()
            }

        }
//-----------------------------댓글------------------------------------------------------
        val recyclerView:RecyclerView=binding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.adapter=adapter

        var text=""
        binding.putComment.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                text=binding.putComment.text.toString()
            }
        })

        binding.commentButton.setOnClickListener{
            val newComment=NewComment(myID, postingID, text)
            addComment(newComment)
            binding.putComment.setText("")
        }
    }
    private fun viewPosting(postingId:Long, memberId: Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewPosting(postingId = postingId , memberId=memberId ) ?:return

        call.enqueue(object :Callback<Post>{

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "그룹 커뮤니티 세부 글 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", response.body().toString())
                postTitle=response.body()!!.title
                postContent=response.body()!!.content
                postWriter=response.body()!!.nickname
                writerID=response.body()!!.memberId
                createdDate=response.body()!!.createdDate
                lastModifiedDate=response.body()!!.lastModifiedDate
                // postWriterImage=intent.getIntExtra("profileID", 0) //todo 사용자 이미지 표시
                comments=response.body()!!.comments
                adapter=MyAdapter(comments)
                postCapacity=response.body()!!.capacity
                closingCheck=response.body()!!.closingCheck
                recruitingList=response.body()!!.recruitingList
                participants=response.body()!!.participants
                bookMarkId=response.body()!!.checkBookmarkId
                recruitingId=response.body()!!.checkRecruitingId
                if(bookMarkId==0.toLong()) binding.markButton.text="북마크"
                else binding.markButton.text="저장됨"
                if(recruitingId==0.toLong()) binding.joinButton.text="참여하기"
                else binding.joinButton.text="참여 취소"
                attach()
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "그룹 커뮤니티 세부 글 - 응답 실패 / t: $t")
            }
        })
    }
    private fun deletePosting(PostingID:Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= PostingID ?:0
        val call = iRetrofit?.deletePosting(postingId = term) ?:return

        call.enqueue(object : Callback<Long>{
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
    private fun joinGroup(PostingID:Long?, memberId:Long, acceptCheck:AcceptCheck){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= PostingID ?:0
        val call = iRetrofit?.joinGroup(postingId = term, memberId=memberId, acceptCheck = acceptCheck) ?:return
        call.enqueue(object : Callback<Join>{
            //응답성공
            override fun onResponse(call: Call<Join>, response: Response<Join>) {
                Log.d("retrofit", "참여하기 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", "참여하기 - 응답 성공 / t : ${response.body().toString()}")
                onResume()
            }
            //응답실패
            override fun onFailure(call: Call<Join>, t: Throwable) {
                Log.d("retrofit", "참여하기 - 응답 실패 / t: $t")
            }
        })
    }
    private fun cancelJoinGroup(recruitingId: Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= recruitingId ?:0
        val call = iRetrofit?.cancelJoinGroup(recruitingId=term) ?:return

        call.enqueue(object : Callback<Long>{
            //응답성공
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("retrofit", "참여하기 신청 취소 - 응답 성공 / t : ${response.raw()}")
                onResume()
            }
            //응답실패
            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit", "참여하기 신청 취소 - 응답 실패 / t: $t")
            }
        })
    }
    private fun addBookMark(BookMark: BookMark){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.addBookMark(BookMark= BookMark) ?:return

        call.enqueue(object : Callback<MarkId>{
            //응답성공
            override fun onResponse(call: Call<MarkId>, response: Response<MarkId>) {
                Log.d("retrofit", "북마크 추가 - 응답 성공 / t : ${response.body().toString()}")
                bookMarkId=response.body()!!.bookmark_id
                onResume()
            }
            //응답실패
            override fun onFailure(call: Call<MarkId>, t: Throwable) {
                Log.d("retrofit", "북마크 추가 - 응답 실패 / t: $t")
            }
        })
    }
    private fun deleteBookMark(bookMarkId: Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= bookMarkId ?:0
        val call = iRetrofit?.deleteBookMark(bookmarkId=term) ?:return

        call.enqueue(object : Callback<Long>{
            //응답성공
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("retrofit", "북마크 삭제 - 응답 성공 / t : ${response.raw()}")
                onResume()
            }
            //응답실패
            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit", "북마크 삭제 - 응답 실패 / t: $t")
            }
        })
    }
    private fun addComment(comment: NewComment){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.addComment(comment) ?:return

        call.enqueue(object : Callback<Comment>{
            //응답성공
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                Log.d("retrofit", "댓글 추가 - 응답 성공 / t : ${response.raw()}")
                onResume()
            }
            //응답실패
            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Log.d("retrofit", "댓글 추가 - 응답 실패 / t: $t")
            }
        })
    }
    private fun editComment(commentId: Long?, comment: NewComment){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= commentId ?:0
        val call = iRetrofit?.editComment(commentId=term, comment) ?:return

        call.enqueue(object : Callback<Comment>{
            //응답성공
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                Log.d("retrofit", "댓글 수정 - 응답 성공 / t : ${response.raw()}")
                onResume()
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

        call.enqueue(object : Callback<Long>{
            //응답성공
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("retrofit", "댓글 삭제 - 응답 성공 / t : ${response.raw()}")
                onResume()
            }
            //응답실패
            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit", "댓글 삭제 - 응답 실패 / t: $t")
            }
        })
    }
    override fun onResume() {
        super.onResume()
        viewPosting(intent.getLongExtra("postingId", 1), myID)
    }
}