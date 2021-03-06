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
        myID=1 //todo ??? id ????????????

        //?????? ?????????
        val postTitle=intent.getStringExtra("title")
        val postContent=intent.getStringExtra("content")
        val postWriter=intent.getStringExtra("nickname")
        val createdDate=intent.getStringExtra("createdDate")
        val lastModifiedDate=intent.getStringExtra("lastModifiedDate")
        //val postWriterImage=intent.getIntExtra("profileID", 0) //todo ????????? ????????? ??????

        val token=createdDate!!.split("-", "T", ":")
        val writtenTime=token[1]+"/"+token[2]+" "+token[3]+"/"+token[4]
        val modifiedWrittenTime= "$writtenTime (?????????)"

        if(createdDate == lastModifiedDate) binding.writtenTime.text=writtenTime
        else binding.writtenTime.text=modifiedWrittenTime

        binding.postTitle.text=postTitle
        binding.postContent.text=postContent
        binding.writerName.text=postWriter
        //binding.writerImage.setImageResource(postWriterImage)

        binding.writerImage.setOnClickListener{ //????????? ????????? ??????
            val userProfile = Intent(this, ShowUserProfileActivity::class.java)
            userProfile.putExtra("memberId",writerID)
            startActivity(userProfile)
        }
        //-----------------------------------------------------?????? ??????-------------------------------------------------
        binding.backButton.setOnClickListener{ //??? ??????????????? ??????
            finish()
        }

        val editButton=binding.editButton
        val deleteButton=binding.deleteButton
        val markButton=binding.markButton

        if(writerID != myID){ //?????? ??? ?????? ????????? ?????? ?????? ??????
            editButton.visibility= INVISIBLE
            deleteButton.visibility= INVISIBLE
        }else{
            markButton.visibility= INVISIBLE //?????? ??? ????????? ????????? ?????? ??? ??????
            editButton.setOnClickListener{
                val postDetail= Intent(this, PostingEdit::class.java)
                postDetail.putExtra("postingId", postingId)
                postDetail.putExtra("title", postTitle)
                postDetail.putExtra("content", postContent)
                startActivity(postDetail)
                finish()
            }
            deleteButton.setOnClickListener{
                val dialog = ConfirmDialog(this, "?????? ?????????????????????????", 0,0)
                dialog.isCancelable = false
                dialog.show(this.supportFragmentManager, "ConfirmDialog")
            }
            markButton.setOnClickListener{
                if (mark){//????????? ??????
                    val dialog = ConfirmDialog(this, "???????????? ?????????????????????.", 1,1)
                    dialog.isCancelable = false
                    dialog.show(this.supportFragmentManager, "ConfirmDialog")
                    deleteBookMark(bookMarkId)
                    markButton.text="?????????"
                    mark=false
                } else{//????????? ??????
                    val dialog = ConfirmDialog(this, "???????????? ??????????????????.", 2,1)
                    dialog.isCancelable = false
                    dialog.show(this.supportFragmentManager, "ConfirmDialog")
                    val newBookMark=BookMark(myID, postingId, bookMarkId)
                    addBookMark(newBookMark)
                    markButton.text="?????????"
                    mark=true
                }
            }
        }

        //-----------------------------??????------------------------------------------------------
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

            image.setOnClickListener{ //?????? ????????? ????????? ??????
                val userProfile = Intent(this@PostDetail_Main, ShowUserProfileActivity::class.java)
                userProfile.putExtra("memberId",this.comment.member.memberId)
                startActivity(userProfile)
            }

            itemView.setOnLongClickListener{
                val items=arrayOf("??????", "??????")
                val builder = AlertDialog.Builder(this@PostDetail_Main)
                builder.setTitle("?????? ??????")
                builder.setItems(items){ _, which ->
                    if(which == 0) {
                        binding.putComment.setText(this.comment.text) //?????? ??? ????????? ?????????
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
//------------------------------------API ??????-------------------------------------------

    private fun deletePosting(PostingID:Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= PostingID ?:0
        val call = iRetrofit?.deletePosting(postingId = term) ?:return

        call.enqueue(object : retrofit2.Callback<Long>{
            //????????????
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("retrofit", "??? ?????? - ?????? ?????? / t : ${response.raw()}")
                finish()
            }
            //????????????
            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit", "??? ?????? - ?????? ?????? / t: $t")
            }
        })
    }

    private fun addComment(comment: NewComment){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.addComment(comment) ?:return

        call.enqueue(object : retrofit2.Callback<NewComment>{
            //????????????
            override fun onResponse(call: Call<NewComment>, response: Response<NewComment>) {
                Log.d("retrofit", "?????? ?????? - ?????? ?????? / t : ${response.raw()}")
            }
            //????????????
            override fun onFailure(call: Call<NewComment>, t: Throwable) {
                Log.d("retrofit", "?????? ?????? - ?????? ?????? / t: $t")
            }
        })
    }
    private fun editComment(commentId: Long?, comment: NewComment){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= commentId ?:0
        val call = iRetrofit?.editComment(commentId=term, comment) ?:return

        call.enqueue(object : retrofit2.Callback<Comment>{
            //????????????
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                Log.d("retrofit", "?????? ?????? - ?????? ?????? / t : ${response.raw()}")
            }
            //????????????
            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Log.d("retrofit", "?????? ?????? - ?????? ?????? / t: $t")
            }
        })
    }
    private fun deleteComment(commentId: Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= commentId ?:0
        val call = iRetrofit?.deleteComment(commentId=term) ?:return

        call.enqueue(object : retrofit2.Callback<Long>{
            //????????????
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("retrofit", "?????? ?????? - ?????? ?????? / t : ${response.raw()}")
            }
            //????????????
            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit", "?????? ?????? - ?????? ?????? / t: $t")
            }
        })
    }
    private fun addBookMark(BookMark: BookMark){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.addBookMark(BookMark) ?:return

        call.enqueue(object : retrofit2.Callback<BookMark>{
            //????????????
            override fun onResponse(call: Call<BookMark>, response: Response<BookMark>) {
                Log.d("retrofit", "????????? ?????? - ?????? ?????? / t : ${response.raw()}")
                bookMarkId=response.body()!!.bookmarkId
            }
            //????????????
            override fun onFailure(call: Call<BookMark>, t: Throwable) {
                Log.d("retrofit", "????????? ?????? - ?????? ?????? / t: $t")
            }
        })
    }
    private fun deleteBookMark(bookMarkId: Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= bookMarkId ?:0
        val call = iRetrofit?.deleteBookMark(bookMarkId=term) ?:return

        call.enqueue(object : retrofit2.Callback<Long>{
            //????????????
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("retrofit", "????????? ?????? - ?????? ?????? / t : ${response.raw()}")
            }
            //????????????
            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit", "????????? ?????? - ?????? ?????? / t: $t")
            }
        })
    }
}