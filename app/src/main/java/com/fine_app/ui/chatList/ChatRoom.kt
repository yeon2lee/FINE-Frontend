package com.fine_app.ui.chatList

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.*
import com.fine_app.databinding.ChattingRoomBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
import kotlin.properties.Delegates

class ChatRoom: AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener {
    private val TAG = "ChatListFragment"

    private lateinit var binding: ChattingRoomBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private var soloCheck=true
    private lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    private var roomId by Delegates.notNull<Long>()
    private val memberId = 2L

    private var mStompClient: StompClient? = null
    private var compositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChattingRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        roomId=intent.getLongExtra("roomId", 0)
        viewChatting()

        mStompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP, "ws://" + "54.209.17.39" + ":" + "8080" + "/ws-fine" + "/websocket"
        )
        //방에 입장
        connectStomp()
        enter()

        binding.backButton.setOnClickListener{
            exit()
            disconnectStomp()
            finish()
        }


        var text=""
        binding.putChat.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text=binding.putChat.text.toString()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text=binding.putChat.text.toString()
            }
            override fun afterTextChanged(p0: Editable?) {
                text=binding.putChat.text.toString()
            }
        })
        binding.sendButton.setOnClickListener{
            sendText(text)
            text=""
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.hide()
        drawerLayout = findViewById(R.id.drawer_layout)

        binding.menuButton.setOnClickListener {
            viewMemberList()
        }
        //navigationView = findViewById(R.id.nav_view)
        //navigationView.setNavigationItemSelectedListener(this)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        exit()
        disconnectStomp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("retrofit", "인텐트 종료")
        if (resultCode == RESULT_OK) {
            val roomName = data?.getStringExtra("text")!!
            Log.d("ff", "방 이름: ${roomName}")
            PutRoomName(ChangeRoomName(memberId, roomId, roomName))
            finish()
        }
    }


    // 드로어 내 아이템 클릭 이벤트 처리하는 함수
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_item1-> {
                val create= Intent(this, CreateRoomName::class.java)
                startActivityForResult(create, 100)
            }
        }
        return false
    }

    inner class OtherViewHolder(view:View): RecyclerView.ViewHolder(view){
        private lateinit var chat: ChatMessage
        private val nickname: TextView =itemView.findViewById(R.id.sender_name)
        private val profileImage: ImageView =itemView.findViewById(R.id.imageView16)
        private val unReadNumber: TextView =itemView.findViewById(R.id.unreadNumber)
        private val sendTime: TextView =itemView.findViewById(R.id.sendTime)
        private val chatText: TextView =itemView.findViewById(R.id.chatText)

        fun bind(chat: ChatMessage) {
            this.chat=chat
            val token= this.chat.createdDate.split("-", "T", ":")
            sendTime.text=token[1]+"/"+token[2]+" "+token[3]+":"+token[4]
            if(this.chat.unreadCount!=0) unReadNumber.text=this.chat.unreadCount.toString()
            chatText.text=this.chat.message
            nickname.text=this.chat.sender.nickname
            var imageResource = this.chat.sender.userImageNum
            when (imageResource) {
                0 -> profileImage.setImageResource(R.drawable.profile)
                1 -> profileImage.setImageResource(R.drawable.profile1)
                2 -> profileImage.setImageResource(R.drawable.profile2)
                3 -> profileImage.setImageResource(R.drawable.profile3)
                4 -> profileImage.setImageResource(R.drawable.profile4)
                5 -> profileImage.setImageResource(R.drawable.profile5)
                6 -> profileImage.setImageResource(R.drawable.profile6)
                else -> profileImage.setImageResource(R.drawable.profile)
            }
        }
    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){
        private lateinit var chat: ChatMessage
        private val unReadNumber: TextView =itemView.findViewById(R.id.unreadNumber)
        private val sendTime: TextView =itemView.findViewById(R.id.sendTime)
        private val chatText: TextView =itemView.findViewById(R.id.chatText)

        fun bind(chat: ChatMessage) {
            this.chat=chat
            val token= this.chat.createdDate.split("-", "T", ":")
            sendTime.text=token[1]+"/"+token[2]+" "+token[3]+":"+token[4]
            if(this.chat.unreadCount!=0) unReadNumber.text=this.chat.unreadCount.toString()
            chatText.text=this.chat.message
        }
    }
    inner class MyAdapter(val list:List<ChatMessage>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

    inner class SideViewHolder(view:View): RecyclerView.ViewHolder(view){
        private lateinit var friend: ChangeChatRoom
        private val friendImage: ImageView =itemView.findViewById(R.id.friend_image)
        private val friendName: TextView =itemView.findViewById(R.id.friend_name)
        private val now: TextView =itemView.findViewById(R.id.now)
        private val button: Button =itemView.findViewById(R.id.button2)

        fun bind(friend: ChangeChatRoom) {
            this.friend=friend
            friendName.text=this.friend.member.nickname
            if(this.friend.presentPosition) now.setTextColor(Color.parseColor("#6DB33F"))
            if(this.friend.member.id==memberId){
                friendName.text="(나) ${this.friend.member.nickname}"
                now.visibility=View.INVISIBLE
                button.visibility=View.INVISIBLE
            }
            val imageResource = this.friend.member.userImageNum
            when (imageResource) {
                0 -> friendImage.setImageResource(R.drawable.profile)
                1 -> friendImage.setImageResource(R.drawable.profile1)
                2 -> friendImage.setImageResource(R.drawable.profile2)
                3 -> friendImage.setImageResource(R.drawable.profile3)
                4 -> friendImage.setImageResource(R.drawable.profile4)
                5 -> friendImage.setImageResource(R.drawable.profile5)
                6 -> friendImage.setImageResource(R.drawable.profile6)
                else -> friendImage.setImageResource(R.drawable.profile)
            }
        }
    }
    inner class SideAdapter(val list:List<ChangeChatRoom>): RecyclerView.Adapter<SideViewHolder>() {
        override fun getItemCount(): Int = list.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SideViewHolder {
            val view=layoutInflater.inflate(R.layout.item_sidebar, parent, false)
            return SideViewHolder(view)
        }
        override fun onBindViewHolder(holder: SideViewHolder, position: Int) {
            val friend=list[position]
            holder.bind(friend)

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
                    val text= topicMessage.payload //todo 이 text를 recyclerView에 입히기
                    val roomId=JSONObject(text).getString("roomId")
                    val memberId:Long=JSONObject(text).getString("memberId").toLong()
                    val nickName=JSONObject(text).getString("nickName")
                    val message=JSONObject(text).getString("message")
                    val unreadCount=JSONObject(text).getString("unreadCount").toInt()
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

    private fun viewMemberList(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewMemberList(roomId=roomId) ?:return

        call.enqueue(object : Callback<ChatMember> {

            override fun onResponse(call: Call<ChatMember>, response: Response<ChatMember>) {
                Log.d("retrofit", "채팅 멤버 목록 - 응답 성공 / t : ${response.raw()}")
                if(response.body()!=null){
                    recyclerView2=findViewById(R.id.chatMemberRecyclerView)
                    recyclerView2.layoutManager= LinearLayoutManager(this@ChatRoom)
                    recyclerView2.adapter=SideAdapter(response.body()!!.chatMemberList)
                    drawerLayout.openDrawer(GravityCompat.END)
                }
            }

            override fun onFailure(call: Call<ChatMember>, t: Throwable) {
                Log.d("retrofit", "채팅 멤버 목록 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewChatting(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewChatting(roomId=roomId, memberId = memberId) ?:return

        call.enqueue(object : Callback<Chat> {
            override fun onResponse(call: Call<Chat>, response: Response<Chat>) {
                Log.d("retrofit", "메인 채팅방 내부 - 응답 성공 / t : ${response.raw()}")

                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(this@ChatRoom)
                recyclerView.adapter=MyAdapter(response.body()!!.chatMessageList)

                soloCheck=response.body()!!.soloCheck
                if(soloCheck){
                    binding.memberNum.visibility=View.GONE
                }else{
                    binding.memberNum.text=response.body()!!.memberCount.toString()
                }
                binding.roomName.text=response.body()!!.roomName


            }
            override fun onFailure(call: Call<Chat>, t: Throwable) {
                Log.d("retrofit", "메인 채팅방 내부  - 응답 실패 / t: $t")
            }
        })
    }
    private fun PutRoomName(Info:ChangeRoomName){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.changeRoomName(Info) ?:return

        call.enqueue(object : Callback<ChangeChatRoom> {
            override fun onResponse(call: Call<ChangeChatRoom>, response: Response<ChangeChatRoom>) {
                Log.d("retrofit", "채팅방 이름 변경 - 응답 성공 / t : ${response.raw()}")
            }
            override fun onFailure(call: Call<ChangeChatRoom>, t: Throwable) {
                Log.d("retrofit", "채팅방 이름 변경  - 응답 실패 / t: $t")
            }
        })
    }
}
