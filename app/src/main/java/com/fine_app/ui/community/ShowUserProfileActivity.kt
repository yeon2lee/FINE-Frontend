package com.fine_app.ui.community

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.fine_app.Friend
import com.fine_app.R
import com.fine_app.databinding.ActivityShowUserProfileBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.myPage.Profile
import com.fine_app.ui.myPage.ServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class ShowUserProfileActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityShowUserProfileBinding
    private var userId:Long=0
    lateinit var userData: Profile
    lateinit var friendList: List<Friend>
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId= intent.getLongExtra("memberId", 1.toLong())
        getUserProfile()

        _binding = ActivityShowUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.profileUserFollowBtn.setOnClickListener {
            if (binding.profileUserFollowBtn.text.toString().equals("팔로우")) {
                followFriend(userId, 2) // TODO: 내 아이디 불러오기
                binding.profileUserFollowBtn.setBackgroundColor(Color.parseColor("#FFFFFF"))
                binding.profileUserFollowBtn.setText("팔로우 취소")
                binding.profileUserFollowBtn.setTextColor(Color.parseColor("#615A55"))
            } else { // 팔로우 취소
                cancelFollow(userId, 2) // TODO: 내 아이디 불러오기
                binding.profileUserFollowBtn.setBackgroundColor(Color.parseColor("#6DB33F"))
                binding.profileUserFollowBtn.setText("팔로우")
                binding.profileUserFollowBtn.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }
    }

    private fun getUserProfile() {
        val call: Call<Profile> = ServiceCreator.service.getMyProfile(userId)
        call.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    userData = response.body()!!
                    binding.profileUserNicknameTv.text = userData.nickname
                    binding.profileUserIntroTv.text = userData.intro
                    var imageResource = userData.userImageNum
                    binding.profileUserFriendNumTv.text = userData.follower.toString()
                    when (imageResource) {
                        0 -> binding.profileUserImageIv.setImageResource(R.drawable.profile1)
                        1 -> binding.profileUserImageIv.setImageResource(R.drawable.profile1)
                        2 -> binding.profileUserImageIv.setImageResource(R.drawable.profile2)
                        3 -> binding.profileUserImageIv.setImageResource(R.drawable.profile3)
                        4 -> binding.profileUserImageIv.setImageResource(R.drawable.profile4)
                        5 -> binding.profileUserImageIv.setImageResource(R.drawable.profile5)
                        6 -> binding.profileUserImageIv.setImageResource(R.drawable.profile6)
                        else -> binding.profileUserImageIv.setImageResource(R.drawable.profile1)
                    }

                    // 내 프로필일 경우
                    if (userId.toInt() == 2) { // TODO: 내 아이디 불러오기
                        binding.profileUserFollowBtn.setVisibility(View.INVISIBLE)
                    } else {
                        binding.profileUserFollowBtn.setVisibility(View.VISIBLE)
                    }

                    // 친구 팔로우 상태 표시
                    viewFriendList(2.toLong()) // TODO: 내 아이디 불러오기
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
                                binding.profileUserFollowBtn.setBackgroundColor(Color.parseColor("#FFFFFF"))
                                binding.profileUserFollowBtn.setText("팔로우 취소")
                                binding.profileUserFollowBtn.setTextColor(Color.parseColor("#615A55"))
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