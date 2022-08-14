package com.fine_app.ui.chatList

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
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

class CreateGroupChatRoom: AppCompatActivity(), ConfirmDialogInterface {

    private lateinit var binding: ChattingCreateBinding
    private lateinit var recyclerView2: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private val myId:Long=2 // TODO: 내 아이디 불러오기
    private var receiverId by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChattingCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewFriendList(myId)
    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){
        private lateinit var friend: Friend

        fun bind(friend: Friend){
            this.friend=friend

        }
    }
    inner class MyAdapter(private val list:MutableList<Friend>): RecyclerView.Adapter<MyViewHolder2>(){
        val selectionList=mutableListOf<Friend>()
        val onItemClickListener:((MutableList<Friend>)->Unit) ?= null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder2 {
            val view=layoutInflater.inflate(R.layout.item_create_chat, parent, false)
            return MyViewHolder2(view)
        }
        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
            val friend=list[position]
            holder.bind(friend)
        }
    }
    inner class MyViewHolder2(view:View): RecyclerView.ViewHolder(view){
        private lateinit var friend: Friend
        private val friendProfileImage:ImageView=itemView.findViewById(R.id.friend_image) //todo 친구 프로필 이미지
        private val friendLevelImage:ImageView=itemView.findViewById(R.id.friend_level) //todo 레벨 이미지 정해야함
        private val friendName:TextView=itemView.findViewById(R.id.friend_name)
        private val friendIntro:TextView=itemView.findViewById(R.id.friend_intro)
        private val checkBox:CheckBox=itemView.findViewById(R.id.checkBox)
        val selectionList=mutableListOf<Friend>()

        fun bind(friend: Friend){
            this.friend=friend
            friendName.text=this.friend.nickname
            friendIntro.text=this.friend.intro
            checkBox.setOnClickListener(object : CompoundButton.OnCheckedChangeListener,
                View.OnClickListener {
                override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                    if(isChecked) selectionList.add(friend)
                    else selectionList.remove(friend)
                }
                override fun onClick(p0: View?) {}
            })
            itemView.setOnClickListener{
                if(checkBox.isChecked){
                    selectionList.remove(friend)
                    checkBox.isChecked=false
                }
                else{
                    selectionList.add(friend)
                    checkBox.isChecked=true
                }
                if(!selectionList.isEmpty()){
                    recyclerView2=binding.recyclerView
                    recyclerView2.layoutManager= LinearLayoutManager(this@CreateGroupChatRoom, LinearLayoutManager.HORIZONTAL, false)
                    recyclerView2.adapter=MyAdapter(selectionList)
                }
            }
        }
    }
    inner class MyAdapter2(private val list:MutableList<Friend>): RecyclerView.Adapter<MyViewHolder2>(){
       val selectionList=mutableListOf<Friend>()
       val onItemClickListener:((MutableList<Friend>)->Unit) ?= null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder2 {
            val view=layoutInflater.inflate(R.layout.item_chatting_friendlist, parent, false)
            view.setOnClickListener(object:View.OnClickListener{
                override fun onClick(p0: View?) {
                    val id=p0?.tag
                    if(selectionList.contains(id)) selectionList.remove(id)
                    else selectionList.add(id as Friend)
                    notifyDataSetChanged()
                    onItemClickListener?.let{it(selectionList)}
                }
            })
            return MyViewHolder2(view)
        }
        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
            val friend=list[position]
            holder.bind(friend)
        }
    }
    private fun viewFriendList(memberId:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewFriendList(memberId=memberId) ?:return

        call.enqueue(object : Callback<List<Friend>> {

            override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                Log.d("retrofit", "친구 목록 - 응답 성공 / t : ${response.raw()}")
                recyclerView2=binding.recyclerView
                recyclerView2.layoutManager= LinearLayoutManager(this@CreateGroupChatRoom)
                recyclerView2.adapter=MyAdapter2(response.body()!!.toMutableList())
            }

            override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                Log.d("retrofit", "친구 목록 - 응답 실패 / t: $t")
            }
        })
    }

    override fun onYesButtonClick(num: Int, theme: Int) {
        addPersonalChatRoom(receiverId)
    }

    private fun addPersonalChatRoom(receiverId:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.addPersonalChatRoom(myId = myId, receiverId=receiverId) ?:return

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
}