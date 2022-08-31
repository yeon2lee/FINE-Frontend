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
    private var mStompClient: StompClient? = null
    private var compositeDisposable: CompositeDisposable? = null

    private val roomId:Long = 2
    private var myId by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentChatlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userInfo = this.getActivity()!!.getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
        myId = userInfo.getString("userInfo", "2")!!.toLong()

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

        mStompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP, "ws://" + "54.209.17.39" + ":" + "8080" + "/ws-fine" + "/websocket"
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
                        "Received ${topicMessage.payload}" //note
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
                0 -> roomImage.setImageResource(R.drawable.profile1)
                1 -> roomImage.setImageResource(R.drawable.profile1)
                2 -> roomImage.setImageResource(R.drawable.profile2)
                3 -> roomImage.setImageResource(R.drawable.profile3)
                4 -> roomImage.setImageResource(R.drawable.profile4)
                5 -> roomImage.setImageResource(R.drawable.profile5)
                6 -> roomImage.setImageResource(R.drawable.profile6)
                else -> roomImage.setImageResource(R.drawable.profile1)
            }
            itemView.setOnClickListener{
                val create= Intent(activity, ChatRoom::class.java)
                create.putExtra("roomId" , this.chatroom.roomId)
                startActivity(create)
            }
        }
    }
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