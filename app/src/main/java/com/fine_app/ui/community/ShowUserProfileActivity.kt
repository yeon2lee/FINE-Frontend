package com.fine_app.ui.community

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.fine_app.CreateChatRoom
import com.fine_app.Friend
import com.fine_app.PersonalChat
import com.fine_app.R
import com.fine_app.databinding.ActivityShowUserProfileBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.chatList.ChatRoom
import com.fine_app.ui.chatList.CreateMainChatRoom
import com.fine_app.ui.myPage.Profile
import com.fine_app.ui.myPage.ServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates


class ShowUserProfileActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityShowUserProfileBinding
    private var myId by Delegates.notNull<Long>()
    private var userId by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences
    lateinit var userData: Profile
    lateinit var friendList: List<Friend>
    private val binding get() = _binding
    var roomId by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)
        myId = userInfo.getString("userInfo", "2")!!.toLong()
        userId= intent.getLongExtra("memberId", 2.toLong())
        getUserProfile()

        _binding = ActivityShowUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.profileUserFollowBtn.setOnClickListener {
            if (binding.profileUserFollowBtn.text.trim().toString().equals("팔로우")) {
                followFriend(userId, myId) // TODO: 내 아이디 불러오기
                binding.profileUserFollowBtn.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_follow_cancel), null, null, null)
                binding.profileUserFollowBtn.setText("팔로우 취소")
            } else { // 팔로우 취소
                cancelFollow(userId, myId) // TODO: 내 아이디 불러오기
                binding.profileUserFollowBtn.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_follow), null, null, null)
                binding.profileUserFollowBtn.setText("팔로우")
            }
        }

        binding.profileUserChatBtn.setOnClickListener {
            addPersonalChatRoom(PersonalChat(myId, userId))
        }
    }

    private fun addPersonalChatRoom(Info:PersonalChat){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.addPersonalChatRoom(Info) ?:return
        Log.d("retrofit", "개인 채팅방 생성 - 나: ${myId}, 상대: ${userId}")
        call.enqueue(object : Callback<CreateChatRoom> {
            override fun onResponse(call: Call<CreateChatRoom>, response: Response<CreateChatRoom>) {
                val create= Intent(this@ShowUserProfileActivity, ChatRoom::class.java)
                create.putExtra("roomId" , response.body()?.roomId)
                startActivity(create)
                Log.d("retrofit", "개인 채팅방 생성 - 응답 성공 / t : ${response.raw()}")
                finish()
            }
            override fun onFailure(call: Call<CreateChatRoom>, t: Throwable) {
                Log.d("retrofit", "개인 채팅방 생성 - 응답 실패 / t: $t")
            }
        })
    }

    private fun getUserProfile() {
        val call: Call<Profile> = ServiceCreator.service.getMyProfile(userId)
        call.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    userData = response.body()!!
                    binding.profileUserNicknameTv.text = userData.nickname
                    binding.mypageKeywordNickname.setText(userData.nickname)
                    binding.profileUserIntroTv.text = userData.intro
                    var imageResource = userData.userImageNum
                    binding.profileUserFriendNumTv.text = userData.follower.toString()
                    when (imageResource) {
                        0 -> binding.profileUserImageIv.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        1 -> binding.profileUserImageIv.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        2 -> binding.profileUserImageIv.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                        3 -> binding.profileUserImageIv.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                        4 -> binding.profileUserImageIv.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                        5 -> binding.profileUserImageIv.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                        6 -> binding.profileUserImageIv.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                        else -> binding.profileUserImageIv.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                    }

                    binding.mypageProfileKeyword1.setText(userData.keyword3)

                    if (userData.keyword1 == null) {
                        binding.mypageAuthUniversityIc.setImageResource(R.drawable.ic_auth_add)
                        binding.mypageAuthUniversityTv.setText("학교 인증")
                    } else {
                        binding.mypageAuthUniversityIc.setImageResource(R.drawable.ic_auth_check)
                        binding.mypageAuthUniversityTv.setText(userData.keyword1)
                        binding.mypageAuthUniversityDate.setText("2022.9.15")
                    }

                    if (userData.userPhoneNumber == null) {
                        binding.mypageAuthPhoneIc.setImageResource(R.drawable.ic_auth_add)
                        binding.mypageAuthPhoneTv.setText("번호 인증")
                    } else {
                        binding.mypageAuthPhoneIc.setImageResource(R.drawable.ic_auth_check)
                        binding.mypageAuthPhoneTv.setText(userData.userPhoneNumber)
                        binding.mypageAuthPhoneDate.setText("2022.9.15")
                    }

                    if (userData.keyword2 == null) {
                        binding.mypageAuthLocationIc.setImageResource(R.drawable.ic_auth_add)
                        binding.mypageAuthLocationTv.setText("지역 인증")
                    } else {
                        binding.mypageAuthLocationIc.setImageResource(R.drawable.ic_auth_check)
                        binding.mypageAuthLocationTv.setText(userData.keyword2)
                        binding.mypageAuthLocationDate.setText("2022.9.15")
                    }

                    // 내 프로필일 경우
                    if (userId == myId) { // TODO: 내 아이디 불러오기
                        binding.profileUserFollowBtn.setVisibility(View.GONE)
                        binding.profileUserChatBtn.setVisibility(View.GONE)
                    } else {
                        binding.profileUserFollowBtn.setVisibility(View.VISIBLE)
                        binding.profileUserChatBtn.setVisibility(View.VISIBLE)
                    }

                    // 친구 팔로우 상태 표시
                    viewFriendList(myId) // TODO: 내 아이디 불러오기
                } else {
                    Toast.makeText(this@ShowUserProfileActivity, "프로필 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Toast.makeText(this@ShowUserProfileActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }

        })
    }


    // 팔로우
    private fun followFriend(friendId:Long, memberId:Long) {
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.followFriend(friendId=friendId, memberId=memberId) ?:return

        call.enqueue(object : retrofit2.Callback<List<Friend>>{
            override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                Log.d("retrofit", "친구 팔로잉 - 응답 성공 / t : ${response.raw()}")
            }

            override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                Log.d("retrofit", "친구 목록 - 응답 실패 / t: $t")
            }
        })
    }

    // 팔로우 취소
    private fun cancelFollow(friendId:Long, memberId: Long) {
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.cancelFollow(friendId=friendId, memberId=memberId) ?:return

        call.enqueue(object : retrofit2.Callback<Long>{
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                Log.d("retrofit", "팔로우 취소 - 응답 성공 / t : ${response.raw()}")
                // todo 팔로우 팔로잉 버튼 색깔과 글씨 바꾸기
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.d("retrofit", "팔로우 취소 - 응답 실패 / t: $t")
            }
        })
    }

    private fun viewFriendList(memberId:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewFriendList(memberId=memberId) ?:return

        call.enqueue(object : Callback<List<Friend>>{

            override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                Log.d("retrofit", "친구 목록 - 응답 성공 / t : ${response.raw()}")
                friendList = response.body()!!
                // 친구 팔로우 상태 표시
                if (friendList != null) {
                    for (i in 0..(friendList.size - 1)) {
                        if (friendList.get(i).friendId.equals(userId)) {
                            binding.profileUserFollowBtn.setText("팔로우 취소")
                            break
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                Log.d("retrofit", "친구 목록 - 응답 실패 / t: $t")
            }
        })
    }
}