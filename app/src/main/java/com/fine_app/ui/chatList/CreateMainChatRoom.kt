package com.fine_app.ui.chatList

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.*
import com.fine_app.databinding.ChattingCreateBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.community.ConfirmDialog
import com.fine_app.ui.community.ConfirmDialogInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class CreateMainChatRoom: AppCompatActivity(), ConfirmDialogInterface {

    private lateinit var binding:  ChattingCreateBinding
    private lateinit var recyclerView2: RecyclerView
    private var myId by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences
    private var receiverId by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChattingCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)
        myId = userInfo.getString("userInfo", "2")!!.toLong()
        viewFriendList(myId)
        binding.searchFriend.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {
                searchFriend(p0)
                return true
            }
            override fun onQueryTextChange(p0: String): Boolean {
                searchFriend(p0)
                return true
            }
        })
        binding.backButton2.setOnClickListener {
            finish()
        }
    }

    inner class MyViewHolder2(view:View): RecyclerView.ViewHolder(view){
        private lateinit var friend: Friend
        private val friendProfileImage:ImageView=itemView.findViewById(R.id.friend_image)
        private val friendLevelImage:ImageView=itemView.findViewById(R.id.friend_level) //todo 레벨 이미지 정해야함
        private val friendName:TextView=itemView.findViewById(R.id.friend_name)
        //private val friendIntro:TextView=itemView.findViewById(R.id.friend_intro)

        fun bind(friend: Friend){
            this.friend=friend
            friendName.text=this.friend.nickname
            //friendIntro.text=this.friend.intro
            var imageResource = this.friend.imageNum
            when (imageResource) {
                0 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                1 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                2 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                3 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                4 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                5 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                6 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                else -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
            }
            itemView.setOnClickListener{
                val dialog = ConfirmDialog(this@CreateMainChatRoom, "${this.friend.nickname} 님과 \n1:1 채팅방을 개설하시겠습니까?", 0, 0)
                dialog.isCancelable = false
                receiverId=this.friend.friendId
                dialog.show(this@CreateMainChatRoom.supportFragmentManager, "ConfirmDialog")
            }
        }
    }
    inner class MyAdapter2(private val list:List<Friend>): RecyclerView.Adapter<MyViewHolder2>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder2 {
            val view=layoutInflater.inflate(R.layout.item_chatting_friendlist, parent, false)
            return MyViewHolder2(view)
        }
        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
            val friend=list[position]
            holder.bind(friend)
        }
    }

    override fun onYesButtonClick(num: Int, theme: Int) {

        addPersonalChatRoom(PersonalChat(myId, receiverId))
    }

    private fun addPersonalChatRoom(Info:PersonalChat){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.addPersonalChatRoom(Info) ?:return
        Log.d("retrofit", "개인 채팅방 생성 - 나: ${myId}, 상대: ${receiverId}")
        call.enqueue(object : Callback<CreateChatRoom> {
            override fun onResponse(call: Call<CreateChatRoom>, response: Response<CreateChatRoom>) {
                Log.d("retrofit", "개인 채팅방 생성 - 응답 성공 / t : ${response.raw()}")
                finish()
            }
            override fun onFailure(call: Call<CreateChatRoom>, t: Throwable) {
                Log.d("retrofit", "개인 채팅방 생성 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewFriendList(memberId:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewFriendList(memberId=memberId) ?:return

        call.enqueue(object : Callback<List<Friend>> {

            override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                Log.d("retrofit", "친구 목록 - 응답 성공 / t : ${response.raw()}")
                recyclerView2=binding.recyclerView2
                recyclerView2.layoutManager= LinearLayoutManager(this@CreateMainChatRoom)
                recyclerView2.adapter=MyAdapter2(response.body()!!)
            }

            override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                Log.d("retrofit", "친구 목록 - 응답 실패 / t: $t")
            }
        })
    }
    private fun searchFriend(search:String){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.searchFriend(memberId = myId, search = search) ?:return

        call.enqueue(object : Callback<List<Friend>> {
            override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                Log.d("retrofit", "친구 검색 - 응답 성공 / t : ${response.body().toString()}")
                recyclerView2=binding.recyclerView2
                recyclerView2.layoutManager= LinearLayoutManager(this@CreateMainChatRoom)
                recyclerView2.adapter=MyAdapter2(response.body()!!)
            }
            override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                Log.d("retrofit", "친구 검색 - 응답 실패 / t: $t")
            }
        })
    }
}