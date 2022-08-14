package com.fine_app.ui.chatList

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.split
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.ChatRoom
import com.fine_app.Friend
import com.fine_app.Post
import com.fine_app.R
import com.fine_app.databinding.FragmentChatlistBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.friendList.SearchFriendList
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

class ChatListFragment : Fragment() {

    private val TAG = "ChatListFragment"

    private var _binding: FragmentChatlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private var isFabOpen = false
    private var mStompClient: StompClient? = null
    private var compositeDisposable: CompositeDisposable? = null

    private val roomId = 2L
    private val myId = 2L


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentChatlistBinding.inflate(inflater, container, false)
        val root: View = binding.root
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
            val create= Intent(activity, CreateMainChatRoom::class.java)
            startActivity(create)
        }


        mStompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP, "ws://" + "10.0.2.2" + ":" + "8080" + "/ws-fine" + "/websocket"
        )
        resetSubscriptions()
        return root
    }

    private fun toggleFab() {
        Toast.makeText(this.context, "메인 버튼 클릭!", Toast.LENGTH_SHORT).show()
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

    fun disconnectStomp(view: View?) {
        mStompClient!!.disconnect()
    }

    fun connectStomp() {
        mStompClient!!.withClientHeartbeat(1000).withServerHeartbeat(1000)
        resetSubscriptions()
        val dispLifecycle = mStompClient!!.lifecycle() //note 커넥트 여부 확인
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> toast("Stomp connection opened")
                    LifecycleEvent.Type.ERROR -> {
                        Log.e(
                            TAG,
                            "Stomp connection error",
                            lifecycleEvent.exception
                        )
                        toast("Stomp connection error")
                    }
                    LifecycleEvent.Type.CLOSED -> toast("Stomp connection closed")
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> toast("Stomp failed server heartbeat")
                }
            }
        compositeDisposable!!.add(dispLifecycle)
        val dispTopic =
            mStompClient!!.topic("/sub/message/$roomId") //note 어떤 방에 연결하겠다 -> topic 이 순간부터 수신 가능
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ topicMessage: StompMessage ->
                    Log.d(
                        TAG,
                        "Received $topicMessage"
                    )
                }
                ) { throwable: Throwable? ->
                    Log.e(
                        TAG,
                        "Error on subscribe topic",
                        throwable
                    )
                }

        // 간소화한 버전
//        mStompClient.topic("/sub/message" + roomId)
//                .subscribe(); //note 구독이 되면 정상적으로 실행 되는 상태
        compositeDisposable!!.add(dispTopic)
        mStompClient!!.connect()
    }



    private fun toast(text: String?) {
        Log.i(TAG, text!!)
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }

    private fun resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = CompositeDisposable()
    }

    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){

        private lateinit var chatroom: ChatRoom
        private val roomImage: TextView =itemView.findViewById(R.id.friend_image)
        private val roomName: TextView =itemView.findViewById(R.id.friend_name)
        private val sendTime: TextView =itemView.findViewById(R.id.sendTime)
        private val recentChat: TextView =itemView.findViewById(R.id.recentChat)

        fun bind(chatroom: ChatRoom) {
            this.chatroom=chatroom
            val token= this.chatroom.lastMessageTime.split("-", "T", ":")
            roomName.text=this.chatroom.roomName
            sendTime.text=token[1]+"/"+token[2]+" "+token[3]+":"+token[4] //todo 오늘 날짜 받아와서 수정하기
            recentChat.text=this.chatroom.latestMessage
            itemView.setOnClickListener{
            }
        }
    }
    inner class MyAdapter(val list:List<ChatRoom>): RecyclerView.Adapter<MyViewHolder>() {
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

        call.enqueue(object : Callback<List<ChatRoom>> {

            override fun onResponse(call: Call<List<ChatRoom>>, response: Response<List<ChatRoom>>) {
                Log.d("retrofit", "채팅 목록 - 응답 성공 / t : ${response.raw()}")
                val adapter=MyAdapter(response.body()!!)
                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(context)
                recyclerView.adapter=adapter
            }

            override fun onFailure(call: Call<List<ChatRoom>>, t: Throwable) {
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