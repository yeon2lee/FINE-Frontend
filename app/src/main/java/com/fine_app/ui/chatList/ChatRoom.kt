package com.fine_app.ui.chatList

import android.content.Intent
import android.content.SharedPreferences
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
import com.fine_app.retrofit.isJsonObject
import com.fine_app.ui.community.ConfirmDialog
import com.fine_app.ui.community.ConfirmDialogInterface
import com.fine_app.ui.community.ShowUserProfileActivity
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

class ChatRoom: AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener,
    ConfirmDialogInterface {
    var mStompClient: StompClient? = Stomp.over(
        Stomp.ConnectionProvider.OKHTTP, "ws://" + "54.209.17.39" + ":" + "8080" + "/ws-fine" + "/websocket"
    )

    private var compositeDisposable: CompositeDisposable? = null
    private val TAG = "Chatroom"

    private lateinit var binding: ChattingRoomBinding
    lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView2: RecyclerView //사이드바
    private var soloCheck=true
    lateinit var drawerLayout: DrawerLayout
    var roomId by Delegates.notNull<Long>()
    private var memberId by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences
    var exitMember by Delegates.notNull<Long>()
    var ownerId by Delegates.notNull<Long>()
    val oldMessage = ArrayList<SendChat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChattingRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)
        memberId = userInfo.getString("userInfo", "2")!!.toLong()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        roomId=intent.getLongExtra("roomId", 0)


        viewChatting()
        connectStomp()
        enter()
        binding.backButton.setOnClickListener{
            Log.d("ddddd", "뒤로가기")
            exit()
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
            //com.fine_app.ui.Stomp().sendText(text, roomId, memberId)
            binding.putChat.setText("")
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.hide()
        drawerLayout = findViewById(R.id.drawer_layout)

        binding.menuButton.setOnClickListener {
            viewMemberList()
        }

        binding.sideBar.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){ //사이드바 열려있을 때
            drawerLayout.closeDrawers()
        }else { //채팅방 화면일 때
            exit()
            disconnectStomp()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("retrofit", "인텐트 종료")
        if (resultCode == RESULT_OK) {
            val roomName = data?.getStringExtra("text")!!
            val imageNum = data?.getIntExtra("imageNum", 0)
            Log.d("ff", "방 이름: ${roomName}")
            PutRoomName(ChangeRoomName(memberId, roomId, roomName, imageNum))
        }
    }

    // 드로어 내 아이템 클릭 이벤트 처리하는 함수
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("drawer", "아이템 클릭 아아")
        when(item.itemId){
            R.id.menu_item1-> {
                val create= Intent(this, CreateRoomName::class.java)
                startActivityForResult(create, 100)
            }
            R.id.menu_item2-> {
                val dialog = ConfirmDialog(this, "채팅방에서 나가시겠습니까?\n모든 대화 기록이 사라집니다.", 0, 0)
                dialog.isCancelable = false
                dialog.show(this.supportFragmentManager, "ConfirmDialog")
            }
        }
        return false
    }
    override fun onYesButtonClick(num: Int, theme: Int) {
        when (num) {
            0 -> {
                exitChatroom(memberId)
                finish()
            }
            1 -> {
                exitChatroom(exitMember)
                drawerLayout.closeDrawers()
                viewChatting()
            }
        }
    }
    inner class OtherViewHolder(view:View): RecyclerView.ViewHolder(view){
        private lateinit var chat: SendChat
        private val nickname: TextView =itemView.findViewById(R.id.sender_name)
        private val profileImage: ImageView =itemView.findViewById(R.id.imageView16)
        private val unReadNumber: TextView =itemView.findViewById(R.id.unreadNumber)
        private val sendTime: TextView =itemView.findViewById(R.id.sendTime)
        private val chatText: TextView =itemView.findViewById(R.id.chatText)

        fun bind(chat: SendChat) {
            this.chat=chat
            val token= this.chat.createdTime.split("-", "T", ":")
            sendTime.text=token[1]+"/"+token[2]+" "+token[3]+":"+token[4]
            if(this.chat.unreadCount!=0) unReadNumber.text=this.chat.unreadCount.toString()
            chatText.text=this.chat.message
            nickname.text=this.chat.nickname
            when (this.chat.imageNum) {
                0 -> profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                1 -> profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                2 -> profileImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                3 -> profileImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                4 -> profileImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                5 -> profileImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                6 -> profileImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                else -> profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
            }

            profileImage.setOnClickListener{ //작성자 프로필 조회
                val userProfile = Intent(this@ChatRoom, ShowUserProfileActivity::class.java)
                userProfile.putExtra("memberId",this.chat.memberId)
                startActivity(userProfile)
            }
        }
    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){
        private lateinit var chat: SendChat
        private val unReadNumber: TextView =itemView.findViewById(R.id.unreadNumber)
        private val sendTime: TextView =itemView.findViewById(R.id.sendTime)
        private val chatText: TextView =itemView.findViewById(R.id.chatText)

        fun bind(chat: SendChat) {
            this.chat=chat
            val token= this.chat.createdTime.split("-", "T", ":")
            sendTime.text=token[1]+"/"+token[2]+" "+token[3]+":"+token[4]
            if(this.chat.unreadCount!=0) unReadNumber.text=this.chat.unreadCount.toString()
            chatText.text=this.chat.message
        }
    }
    inner class MyAdapter(val list:ArrayList<SendChat>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int = list.size

        override fun getItemViewType(position: Int): Int {
            return if(list[position].memberId == memberId) 1
            else 2
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if(viewType==1)
                MyViewHolder(layoutInflater.inflate(R.layout.item_chatting_my, parent, false))
            else
                OtherViewHolder(layoutInflater.inflate(R.layout.item_chatting_group, parent, false))
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(list[position].memberId == memberId) {
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
            if(memberId!=ownerId){
                button.visibility=View.GONE
            }
            when (this.friend.member.userImageNum) {
                0 -> friendImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                1 -> friendImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                2 -> friendImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                3 -> friendImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                4 -> friendImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                5 -> friendImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                6 -> friendImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                else -> friendImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
            }
            friendImage.setOnClickListener{ //작성자 프로필 조회
                val userProfile = Intent(this@ChatRoom, ShowUserProfileActivity::class.java)
                userProfile.putExtra("memberId",this.friend.member.id)
                startActivity(userProfile)
            }
            button.setOnClickListener {
                exitMember=this.friend.member.id
                val dialog = ConfirmDialog(this@ChatRoom, "${this.friend.member.nickname}님을 채팅방에서 내보내시겠습니까?", 1, 0)
                dialog.isCancelable = false
                dialog.show(this@ChatRoom.supportFragmentManager, "ConfirmDialog")

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
        Log.d("test", "connectStomp 들어옴")
        mStompClient!!.withClientHeartbeat(1000).withServerHeartbeat(1000)
        resetSubscriptions()
        val dispLifecycle = mStompClient!!.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.d("stomp","Stomp connection opened" )
                        //toast("Stomp connection opened")
                    }
                    LifecycleEvent.Type.ERROR -> {
                        Log.e(
                            TAG,
                            "Stomp connection error",
                            lifecycleEvent.exception
                        )
                        Log.d("stomp","Stomp connection error" )
                        //toast("Stomp connection error")
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        Log.d("stomp","Stomp connection closed" )
                        //toast("Stomp connection closed")
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        Log.d("stomp","Stomp failed server heartbeat" )
                        //toast("Stomp failed server heartbeat")
                    }
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
                    val text= topicMessage.payload
                    if(text.isJsonObject()){
                        //val roomId=JSONObject(text).getString("roomId")
                        val memberInfo=JSONObject(text).getJSONObject("memberInfo")
                        val memberId:Long=memberInfo.getString("memberId").toLong()
                        val nickName=memberInfo.getString("nickName")
                        val message=JSONObject(text).getString("message")
                        val unreadCount=JSONObject(text).getString("unreadCount").toInt()
                        val time= JSONObject(text).getString("createdTime")
                        val imageNum=memberInfo.getString("imageNum").toInt()
                        val newMessage=SendChat(memberId, nickName, imageNum, message, unreadCount, time)
                        Log.d("sendChat", "${newMessage}")
                        oldMessage.add(newMessage)
                        Log.d("sendChat", "${oldMessage}")
                        recyclerView.adapter?.notifyItemInserted(oldMessage.size)
                        }
                }
                ) { throwable: Throwable? ->
                    Log.e(
                        TAG,
                        "Error on subscribe topic",
                        throwable
                    )
                }
        compositeDisposable!!.add(dispTopic)
        mStompClient!!.connect()

    }

    fun enter() { //note 방에 들어간 걸 알림  -- 채팅방 화면 보여줌
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "ENTER")
        jsonObject.addProperty("roomId", roomId)
        jsonObject.addProperty("memberId", memberId)
        mStompClient!!.send("/pub/message", jsonObject.toString()) //note pub 송신 sub 수신
            .subscribe({
                Log.d(
                    TAG,
                    "STOMP send successfully"
                )

            }
            ) { throwable: Throwable ->
                Log.e(TAG, "Error send STOMP", throwable)
                Log.d("stomp", "${throwable.message}")
            }
    }

    fun sendText(text:String?) { //note 메세지 보내는 코드
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "TALK")
        jsonObject.addProperty("roomId", roomId)
        jsonObject.addProperty("memberId", memberId)
        jsonObject.addProperty("message", text)
        mStompClient!!.send("/pub/message", jsonObject.toString())
            .subscribe(
                {
                    Log.d("dkdkdk", "sendText 메세지 보냄: ${text}")
                    Log.d(
                        TAG,
                        "STOMP send successfully"
                    )
                }
            ) { throwable: Throwable ->
                Log.d("dkdkdk", "sendText 메세지 전송 실패: ${text}")
                Log.e(TAG, "Error send STOMP", throwable)
                Log.d("stomp", "${throwable.message}")
                //toast(throwable.message)
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
            }
    }

    private fun resetSubscriptions() { //connectStomp에서 호출
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = CompositeDisposable()
    }
    private fun viewMemberList(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewMemberList(roomId=roomId) ?:return

        call.enqueue(object : Callback<ChatMember> {

            override fun onResponse(call: Call<ChatMember>, response: Response<ChatMember>) {
                Log.d("retrofit", "채팅 멤버 목록 - 응답 성공 / t : ${response.raw()}")
                if(response.body()!=null){
                    ownerId=response.body()!!.ownerId
                    recyclerView2=findViewById(R.id.chatMemberRecyclerView)
                    recyclerView2.layoutManager= LinearLayoutManager(this@ChatRoom)
                    recyclerView2.adapter= SideAdapter(response.body()!!.chatMemberList)
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

                //테스트
                val messageList:List<ChatMessage> = response.body()!!.chatMessageList
                Log.d("chat", "${messageList.size}")
                for(i in 0 until messageList.size){ //아이디 닉네임 메세지 카운트 타임
                    val message=SendChat(messageList[i].memberInfo.memberId, messageList[i].memberInfo.nickName, messageList[i].memberInfo.imageNum, messageList[i].message, messageList[i].unreadCount, messageList[i].createdTime)
                    Log.d("chat", "${message}")
                    oldMessage.add(message)
                }
                Log.d("old chat", "${oldMessage}")
                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(this@ChatRoom)
                //recyclerView.adapter=MyAdapter(response.body()!!.chatMessageList)
                recyclerView.adapter= MyAdapter(oldMessage)

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
        val call = iRetrofit?.changeRoomName(Info=Info) ?:return
        Log.d("name", "${Info}")
        call.enqueue(object : Callback<ChangeChatRoom> {
            override fun onResponse(call: Call<ChangeChatRoom>, response: Response<ChangeChatRoom>) {
                Log.d("retrofit", "채팅방 이름 변경 - 응답 성공 / t : ${response.body()!!.toString()}")
            }
            override fun onFailure(call: Call<ChangeChatRoom>, t: Throwable) {
                Log.d("retrofit", "채팅방 이름 변경  - 응답 실패 / t: $t")
            }
        })
    }
    private fun exitChatroom(targetId:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.exitChatroom(roomId=roomId, targetId = targetId) ?:return

        call.enqueue(object : Callback<ExitChat> {

            override fun onResponse(call: Call<ExitChat>, response: Response<ExitChat>) {
                Log.d("retrofit", "채팅방 나가기 - 응답 성공 / t : ${response.raw()}")
            }

            override fun onFailure(call: Call<ExitChat>, t: Throwable) {
                Log.d("retrofit", "채팅방 나가기 - 응답 실패 / t: $t")
            }
        })
    }
}