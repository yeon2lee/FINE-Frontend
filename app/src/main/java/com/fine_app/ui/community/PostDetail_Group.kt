package com.fine_app.ui.community

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Comment
import com.fine_app.Join
import com.fine_app.R
import com.fine_app.databinding.CommunityGroupPostBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostDetail_Group : AppCompatActivity() {
    private lateinit var binding: CommunityGroupPostBinding
    val memberId=1 as Long //todo 멤버아이디 받아오기
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunityGroupPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //정보 받아옴
        val postTitle=intent.getStringExtra("title")
        val postContent=intent.getStringExtra("content")
        val postWriter=intent.getStringExtra("nickname")
        val postCapacity=intent.getIntExtra("capacity", 0)
        val postingID=intent.getLongExtra("postingID", 0)
        val lastModifiedDate=intent.getStringExtra("lastModifiedDate")
        val closingCheck=intent.getBooleanExtra("closingCheck", false)
        //val participants=intent.getIntExtra("participants", 0)//todo 현재 참여인원 받아오기
        //val postWriterImage=intent.getIntExtra("profileID", 0) //todo 사용자 이미지 표시

        binding.postTitle.text=postTitle
        binding.postContent.text=postContent
        binding.writerName.text=postWriter
        binding.max.text=postCapacity.toString()
        //binding.participants.text=participants
        //binding.writerImage.setImageResource(postWriterImage)

        binding.backButton.setOnClickListener{ //글 세부페이지 종료
            finish()
        }
        binding.writerImage.setOnClickListener{
            val userProfile = Intent(this, ShowUserProfileActivity::class.java)
            startActivity(userProfile)
        }
        val editButton=binding.editButton //수정 페이지로 이동
        val deleteButton=binding.deleteButton
        val manageButton=binding.manageButton
        val joinButton=binding.joinButton
        val writerID:Long=0 //todo 글 작성자 id 가져오기
        val myID:Long=0 //todo 내 id 가져오기

        if (closingCheck==true){ //마감된 글이면 //todo 마감은 언제 되는데..?
            joinButton.visibility=View.INVISIBLE
        }else{
            joinButton.visibility=View.VISIBLE
            joinButton.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                    .setMessage("참여하시겠습니까?")
                    .setPositiveButton("확인",
                        DialogInterface.OnClickListener{ dialog, which ->
                            joinGroup(postingID,memberId)
                            finish()
                        })
                    .setNegativeButton("취소",null)
                builder.show()
            }
        }

        if(writerID != myID){ //내가 쓴 글이 아니면 수정 삭제 관리 불가
            editButton.visibility=View.INVISIBLE
            deleteButton.visibility=View.INVISIBLE
            manageButton.visibility=View.INVISIBLE
        }else{
            joinButton.visibility=View.INVISIBLE //내가 쓴 글이면 참여버튼 안 보임
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
            manageButton.setOnClickListener{
                val waitingList = Intent(this, WaitingList::class.java)
                waitingList.putExtra("postingID", postingID) //todo 포스팅아이디 확인
                startActivity(waitingList)
                finish()
            }
        }
//-----------------------------댓글------------------------------------------------------
        val recyclerView:RecyclerView=binding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(this)
        //val comments=commentViewModel.commentList
        val comments= intent.getSerializableExtra("comments") as ArrayList<Comment>
        val adapter=MyAdapter(comments)
        recyclerView.adapter=adapter

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
        //private val image:ImageView=itemView.findViewById(R.id.profileImage)
        fun bind(comment:Comment){
            this.comment=comment
            nickname.text=this.comment.memberId.toString()
            text.text=this.comment.text
            //image.setImageResource(this.comment.profileID)
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
}