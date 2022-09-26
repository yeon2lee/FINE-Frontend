package com.fine_app.ui.community

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.*
import com.fine_app.databinding.CommunityWaitinglistBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class WaitingList : AppCompatActivity(), ConfirmDialogInterface  {
    private lateinit var binding: CommunityWaitinglistBinding
    private lateinit var memberRecyclerView: RecyclerView
    private lateinit var waitingRecyclerView: RecyclerView
    var waitingList=ArrayList<Recruit>()
    var memberList=ArrayList<Recruit>()
    private var postingID by Delegates.notNull<Long>()
    private var capacity by Delegates.notNull<Long>()
    private var myID by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences
    private lateinit var recruitingList:ArrayList<Recruit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)
        myID = userInfo.getString("userInfo", "2")!!.toLong()
        binding = CommunityWaitinglistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener{
            finish()
        }
        postingID=intent.getLongExtra("postingID", 0)
        viewPosting(postingID, myID)
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var crew: Recruit
        private val image: ImageView =itemView.findViewById(R.id.writer_image)
        private val name: TextView =itemView.findViewById(R.id.writer_name)
        private val level: ImageView =itemView.findViewById(R.id.levelImage) //todo 참여자 레벨
        private val button: Button =itemView.findViewById(R.id.acceptButton)

        fun bind(crew: Recruit) {
            this.crew=crew
            name.text=this.crew.member.nickname
            when (this.crew.member.userImageNum) {
                0 -> image.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                1 -> image.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                2 -> image.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                3 -> image.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                4 -> image.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                5 -> image.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                6 -> image.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                else -> image.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
            }
            button.setOnClickListener{
                if(this.crew.acceptCheck) {
                    manageJoinGroup(postingID, this.crew.recruitingId, AcceptCheck(false))
                }
                else {
                    if(memberList.size.toLong() != capacity) { //정원이 다 안 찬 상태
                        manageJoinGroup(postingID, this.crew.recruitingId, AcceptCheck(true))
                    }else{ //정원이 다 찬 상태
                        val dialog = ConfirmDialog(this@WaitingList, "인원을 더 추가할 수 없습니다", 2,1)
                        dialog.show(this@WaitingList.supportFragmentManager, "ConfirmDialog")
                    }
                }
            }
            image.setOnClickListener{
                val userProfile = Intent(this@WaitingList, ShowUserProfileActivity::class.java)
                userProfile.putExtra("memberId",this.crew.member.memberId )
                startActivity(userProfile)
            }
        }
    }
    inner class WaitingAdapter(val list:List<Recruit> ): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_waitlist, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val crew=list[position]
            holder.bind(crew)
        }
    }
    inner class MemberAdapter(val list:List<Recruit> ): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_membertlist, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val crew=list[position]
            holder.bind(crew)
        }
    }

    //------------------------------API 연결------------------------------------
    private fun viewPosting(postingId:Long, memberId: Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewPosting(postingId = postingId , memberId=memberId ) ?:return

        call.enqueue(object :Callback<Post>{

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "그룹 커뮤니티 대기 리스트 - 응답 성공 / t : ${response.raw()}")
                recruitingList=response.body()!!.recruitingList
                memberList.clear()
                waitingList.clear()
                capacity=response.body()!!.capacity.toLong()
                for(i in 0 until recruitingList.size){
                    if(recruitingList[i].acceptCheck) memberList.add(recruitingList[i])
                    else waitingList.add(recruitingList[i])
                }

                memberRecyclerView=binding.memberRecyclerView
                memberRecyclerView.layoutManager= LinearLayoutManager(this@WaitingList)
                memberRecyclerView.adapter=MemberAdapter(memberList)

                waitingRecyclerView=binding.waitingRecyclerView
                waitingRecyclerView.layoutManager= LinearLayoutManager(this@WaitingList)
                waitingRecyclerView.adapter=WaitingAdapter(waitingList)

                if(response.body()!!.closingCheck){
                    binding.closingButton.text="마감 취소"
                    waitingRecyclerView.visibility=View.INVISIBLE
                    binding.textView5.visibility=View.INVISIBLE
                    binding.view15.visibility=View.INVISIBLE
                }else{
                    binding.closingButton.text="글 마감"
                    waitingRecyclerView.visibility=View.VISIBLE
                    binding.textView5.visibility=View.VISIBLE
                    binding.view15.visibility=View.VISIBLE
                }

                binding.closingButton.setOnClickListener{
                    if(response.body()!!.closingCheck){
                        val dialog = ConfirmDialog(this@WaitingList, "마감을 수정할 수 없습니다.", 3,1)
                        dialog.show(this@WaitingList.supportFragmentManager, "ConfirmDialog")
                    }else{
                        val dialog = ConfirmDialog(this@WaitingList, "모집을 마감하시겠습니까?", 4, 0)
                        dialog.isCancelable = false
                        dialog.show(this@WaitingList.supportFragmentManager, "ConfirmDialog")
                    }


                }
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "그룹 커뮤니티 대기 - 응답 실패 / t: $t")
            }
        })
    }
    private fun manageJoinGroup(postingId:Long?, recruitingId:Long, acceptCheck: AcceptCheck){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= postingId ?:0
        val call = iRetrofit?.manageJoinGroup(postingId = term, recruitingId=recruitingId, acceptCheck=acceptCheck) ?:return

        call.enqueue(object : Callback<Join> {
            override fun onResponse(call: Call<Join>, response: Response<Join>) {
                Log.d("retrofit", "참여 수락 변경 - 응답 성공 / t : ${response.raw()}")
                showAgain()
            }
            override fun onFailure(call: Call<Join>, t: Throwable) {
                Log.d("retrofit", "참여 수락 변경 - 응답 실패 / t: $t")
            }
        })
    }
    private fun editClosing(postingId:Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= postingId ?:0
        val call = iRetrofit?.editClosing(postingId = term) ?:return

        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "글 마감 변경 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", "글 마감 변경 - 응답 성공 / t : ${response.body()!!.closingCheck}")
                showAgain()
            }
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "글 마감 변경 - 응답 실패 / t: $t")
            }
        })
    }

    fun showAgain(){
        viewPosting(intent.getLongExtra("postingID", 0), myID)
    }

    override fun onYesButtonClick(num: Int, theme: Int) {
        when(num){
            4->{
                if(memberList.size>1){ //임의로 마감
                    binding.closingButton.isEnabled=true
                    binding.closingButton.text="글 마감"
                    waitingRecyclerView.visibility=View.VISIBLE
                    binding.textView5.visibility=View.VISIBLE
                    binding.view15.visibility=View.VISIBLE
                    editClosing(postingID)
                    val receiverList=ArrayList<Long>()
                    receiverList.clear()
                    for( i in 0..memberList.size-1){
                        receiverList.add(memberList[i].member.memberId)
                    }
                    Log.d("waiting", "receiverList: ${receiverList}")
                    addGroupChatRoom(GroupChat(myID,receiverList.toList(), "그룹 채팅방", 11))
                }else{
                    val dialog = ConfirmDialog(this, "인원을 추가해주세요", 2,1)
                    dialog.show(this.supportFragmentManager, "ConfirmDialog")
                }
            }
        }
    }
    private fun addGroupChatRoom(Info:GroupChat){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.addGroupChatRoom(Info) ?:return

        call.enqueue(object : Callback<CreateChatRoom> {
            override fun onResponse(call: Call<CreateChatRoom>, response: Response<CreateChatRoom>) {
                Log.d("retrofit", "그룹 채팅방 생성 - 응답 성공 / t : ${response.raw()}")
                finish()
            }
            override fun onFailure(call: Call<CreateChatRoom>, t: Throwable) {
                Log.d("retrofit", "그룹 채팅방 생성 - 응답 실패 / t: $t")
            }
        })
    }

}