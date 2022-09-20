package com.fine_app.ui.chatList

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.*
import com.fine_app.databinding.FragmentChatlistBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
import kotlin.properties.Delegates

class ChatListFragment : Fragment() {

    private val TAG = "ChatListFragment"

    private var _binding: FragmentChatlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private var isFabOpen = false
    //private var mStompClient: StompClient? = null
    //var mStompClient=MainActivity().mStompClient
    private var compositeDisposable: CompositeDisposable? = null

    private val roomId:Long = 2
    private var myId by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentChatlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userInfo = this.requireActivity().getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
        myId = userInfo.getString("userInfo", "2")!!.toLong()
        //roomInfo = this.requireActivity().getSharedPreferences("roomInfo", AppCompatActivity.MODE_PRIVATE)

        viewChatList()
        binding.fabMain.setOnClickListener {
            toggleFab()
            Log.d("chat", "기본 클릭")
        }

        binding.fabSolo.setOnClickListener {
            val create= Intent(activity, CreateMainChatRoom::class.java)
            startActivity(create)
        }

        binding.fabGroup.setOnClickListener {
            val create= Intent(activity, CreateGroupChatRoom::class.java)
            startActivity(create)
        }

        /*
        mStompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP, "ws://" + "54.209.17.39" + ":" + "8080" + "/ws-fine" + "/websocket"
        )
        resetSubscriptions()

         */

        return root
    }

    private fun toggleFab() {
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.fabSolo, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabGroup, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION, 45f, 0f).apply { start() }
        } else {
            ObjectAnimator.ofFloat(binding.fabSolo, "translationY", -360f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabGroup, "translationY", -180f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION, 0f, 45f).apply { start() }
        }
        isFabOpen = !isFabOpen
    }

/*
    private fun resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = CompositeDisposable()
    }

 */



    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){

        private lateinit var chatroom: ChatRoomList
        private val roomImage: ImageView =itemView.findViewById(R.id.friend_image)
        private val roomName: TextView =itemView.findViewById(R.id.friend_name)
        private val sendTime: TextView =itemView.findViewById(R.id.sendTime)
        private val recentChat: TextView =itemView.findViewById(R.id.recentChat)
        private val capacity: TextView =itemView.findViewById(R.id.chatRoomCapacity)
        private val unreadCount:TextView=itemView.findViewById(R.id.unreadCount)

        fun bind(chatroom: ChatRoomList) {
            this.chatroom=chatroom
            val token= this.chatroom.lastMessageTime.split("-", "T", ":")
            roomName.text=this.chatroom.roomName
            sendTime.text=token[1]+"/"+token[2]+" "+token[3]+":"+token[4]
            recentChat.text=this.chatroom.latestMessage
            if(this.chatroom.soloCheck){
                capacity.visibility=View.INVISIBLE
            }else{
                capacity.text=this.chatroom.memberCount.toString()
            }
            if(this.chatroom.unreadMessageCount==0) {
                unreadCount.visibility=View.INVISIBLE
            }else{
                unreadCount.text=this.chatroom.unreadMessageCount.toString()
            }
            when (this.chatroom.imageNum) {
                0 -> roomImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                1 -> roomImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                2 -> roomImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                3 -> roomImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                4 -> roomImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                5 -> roomImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                6 -> roomImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                else -> roomImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
            }
            itemView.setOnClickListener{
                //enter(this.chatroom.roomId)
                //com.fine_app.ui.Stomp().enter(this.chatroom.roomId, myId)
                val create= Intent(activity, ChatRoom::class.java)
                create.putExtra("roomId" , this.chatroom.roomId)
                startActivity(create)
            }
        }
    }
    /*
    fun enter(roomId:Long) { //note 방에 들어간 걸 알림  -- 채팅방 화면 보여줌
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "ENTER")
        jsonObject.addProperty("roomId", roomId)
        jsonObject.addProperty("memberId", myId)
        Log.d("dkdkdk", "채팅방 입장 완료 ${jsonObject}")
        Log.d("dkdkdk", "${mStompClient?.isConnected}")
        mStompClient!!.send("/pub/message", jsonObject.toString()) //note pub 송신 sub 수신
            .subscribe({
                Log.d("dkdkdk", "방 입장 완료")
                Log.d(
                    TAG,
                    "STOMP send successfully"
                )

            }
            ) { throwable: Throwable ->
                Log.d("dkdkdk", "방 입장 실패")
                Log.e(TAG, "Error send STOMP", throwable)
                toast(throwable.message)
            }
        Log.d("dkdkdk", "send...")
    }
    private fun toast(text: String?) {
        Log.i(TAG, text!!)
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

     */

    inner class MyAdapter(val list:List<ChatRoomList>): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            val view=layoutInflater.inflate(R.layout.item_chatlist, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }
    private fun viewChatList(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewChatList(memberId=myId) ?:return

        call.enqueue(object : Callback<List<ChatRoomList>> {

            override fun onResponse(call: Call<List<ChatRoomList>>, response: Response<List<ChatRoomList>>) {
                Log.d("retrofit", "채팅 목록 - 응답 성공 / t : ${response.raw()}")
                if(response.body()!=null){
                    val adapter=MyAdapter(response.body()!!)
                    recyclerView=binding.recyclerView
                    recyclerView.layoutManager= LinearLayoutManager(context)
                    recyclerView.adapter=adapter
                }

            }

            override fun onFailure(call: Call<List<ChatRoomList>>, t: Throwable) {
                Log.d("retrofit", "채팅 목록 - 응답 실패 / t: $t")
            }
        })
    }
    override fun onResume() {
        super.onResume()
        viewChatList()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}