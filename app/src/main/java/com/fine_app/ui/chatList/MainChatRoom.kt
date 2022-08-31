package com.fine_app.ui.chatList

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.ChatMessage
import com.fine_app.Post
import com.fine_app.R
import com.fine_app.databinding.ChattingMainBinding
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage

class MainChatRoom: AppCompatActivity() {
    private val TAG = "ChatListFragment"

    private lateinit var binding: ChattingMainBinding
    private lateinit var recyclerView: RecyclerView

    private var mStompClient: StompClient? = null
    private var compositeDisposable: CompositeDisposable? = null

    private val roomId = 2L //todo 클릭이벤트로 룸아이디 받아오기
    private val memberId = 2L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChattingMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        //방에 입장
        connectStomp()
        enter()
        //recyclerView=binding.recyclerView
        //recyclerView.layoutManager= LinearLayoutManager(this)
        //recyclerView.adapter=MyAdapter() //todo 채팅 내역 리스트...받아오기...

        binding.backButton.setOnClickListener{
            exit()
            finish()
        }

        var text=""
        binding.putChat.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                text=binding.putChat.text.toString()
            }
        })
        binding.addButton.setOnClickListener{
            sendText(text)
        }
    }

    inner class OtherViewHolder(view:View): RecyclerView.ViewHolder(view){
        private lateinit var chat: ChatMessage
        private val nickname: TextView =itemView.findViewById(R.id.sender_name)
        private val profileImage: ImageView =itemView.findViewById(R.id.sender_name)
        private val unReadNumber: TextView =itemView.findViewById(R.id.unreadNumber)
        private val sendTime: TextView =itemView.findViewById(R.id.sendTime)
        private val chatText: TextView =itemView.findViewById(R.id.chatText)

        fun bind(chat: ChatMessage) {
            this.chat=chat
            unReadNumber.text=this.chat.unreadCount.toString()
            chatText.text=this.chat.message
            nickname.text=this.chat.sender.nickname
        }
    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){
        private lateinit var chat: ChatMessage
        private val unReadNumber: TextView =itemView.findViewById(R.id.unreadNumber)
        private val sendTime: TextView =itemView.findViewById(R.id.sendTime)
        private val chatText: TextView =itemView.findViewById(R.id.chatText)

        fun bind(chat: ChatMessage) {
            this.chat=chat
            unReadNumber.text=this.chat.unreadCount.toString()
            chatText.text=this.chat.message
        }
    }
    inner class MyAdapter(val list:ArrayList<ChatMessage>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int = list.size

        override fun getItemViewType(position: Int): Int {
            return if(list[position].sender.id == memberId) 1
            else 2
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if(viewType==1)
                MyViewHolder(layoutInflater.inflate(R.layout.item_chatting_my, parent, false))
            else
                OtherViewHolder(layoutInflater.inflate(R.layout.item_chatting_group, parent, false))
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(list[position].sender.id == memberId) {
                (holder as MyViewHolder).bind(list[position])
                holder.setIsRecyclable(false)
            }
            else {
                (holder as OtherViewHolder).bind(list[position])
                holder.setIsRecyclable(false)
            }
        }
    }

    fun disconnectStomp() {
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

    fun enter() { //note 방에 들어간 걸 알림  -- 채팅방 화면 보여줌      //테스트 할 땐 topic이랑 enter 한 번에 처리해놓기
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "ENTER")
        jsonObject.addProperty("roomId", roomId)
        jsonObject.addProperty("memberId", memberId)
        mStompClient!!.send("/pub/message", jsonObject.toString()) //note pub 송신 sub 수신
            .subscribe(
                {
                    Log.d(
                        TAG,
                        "STOMP send successfully"
                    )
                }
            ) { throwable: Throwable ->
                Log.e(TAG, "Error send STOMP", throwable)
                toast(throwable.message)
            }
    }

    private fun sendText(text:String?) { //note 메세지 보내는 코드
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "TALK")
        jsonObject.addProperty("roomId", roomId)
        jsonObject.addProperty("memberId", memberId)
        jsonObject.addProperty("message", text)
        mStompClient!!.send("/pub/message", jsonObject.toString())
            .subscribe(
                {
                    Log.d(
                        TAG,
                        "STOMP send successfully"
                    )
                }
            ) { throwable: Throwable ->
                Log.e(TAG, "Error send STOMP", throwable)
                toast(throwable.message)
            }
    }

    fun exit() { //note 방 나갈 때 사용
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "EXIT")
        jsonObject.addProperty("roomId", roomId)
        jsonObject.addProperty("memberId", memberId)
        mStompClient!!.send("/pub/message", jsonObject.toString())
            .subscribe(
                {
                    Log.d(
                        TAG,
                        "STOMP send successfully"
                    )
                }
            ) { throwable: Throwable ->
                Log.e(TAG, "Error send STOMP", throwable)
                toast(throwable.message)
            }
    }

    private fun resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = CompositeDisposable()
    }

    private fun toast(text: String?) {
        Log.i(TAG, text!!)
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
