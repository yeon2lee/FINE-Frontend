package com.fine_app.ui.myPage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fine_app.Friend
import com.fine_app.R
import com.fine_app.databinding.FragmentMypageBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.myPage.ManagePostActivity
import com.fine_app.ui.myPage.Profile
import com.fine_app.ui.myPage.ServiceCreator
import com.fine_app.ui.myPage.UpdateProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    var userId: Long = 2
    lateinit var friendList: List<Friend>
    lateinit var userData: Profile

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 프로필 생성
        getMyProfile()

        binding.tvManagePost.setOnClickListener {
            activity?.let{
                val intent = Intent(context, ManagePostActivity::class.java)
                startActivity(intent)
            }
        }

        binding.box.setOnClickListener {
            activity?.let{
                val intent = Intent(context, UpdateProfileActivity::class.java)
                intent.putExtra("nickname", binding.tvNickname.text)
                intent.putExtra("intro", binding.tvIntro.text)
                startActivity(intent)
            }
        }

        binding.tvManageBookmark.setOnClickListener() {
            activity?.let{
                val intent = Intent(context, ManageBookmarkActivity::class.java)
                startActivity(intent)
            }
        }

        binding.tvSetting.setOnClickListener() {
            activity?.let{
                val intent = Intent(context, SettingActivity::class.java)
                startActivity(intent)
            }
        }

        binding.mypageProfileAuthPhoneTv.setOnClickListener {
            activity?.let{
                val intent = Intent(context, AuthPhoneActivity::class.java)
                startActivity(intent)
            }
        }

        binding.tvManageGroup.setOnClickListener {
            activity?.let{
                val intent = Intent(context, ManageGroupActivity::class.java)
                startActivity(intent)
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        getMyProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getMyProfile() {
        userId = 1

        val call: Call<Profile> = ServiceCreator.service.getMyProfile(userId)

        call.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    userData = response.body()!!
                    binding.tvNickname.setText(userData.nickname)
                    binding.tvIntro.setText(userData.intro)
                    var imageResource = userData.userImageNum
                    when (imageResource) {
                        0 -> binding.mypageProfileImageIv.setImageResource(R.drawable.profile)
                        1 -> binding.mypageProfileImageIv.setImageResource(R.drawable.profile1)
                        2 -> binding.mypageProfileImageIv.setImageResource(R.drawable.profile2)
                        3 -> binding.mypageProfileImageIv.setImageResource(R.drawable.profile3)
                        4 -> binding.mypageProfileImageIv.setImageResource(R.drawable.profile4)
                        5 -> binding.mypageProfileImageIv.setImageResource(R.drawable.profile5)
                        6 -> binding.mypageProfileImageIv.setImageResource(R.drawable.profile6)
                        else -> binding.mypageProfileImageIv.setImageResource(R.drawable.profile)
                    }
                    binding.mypageProfileLevel.setText("새싹 " + userData.level + "단계")
                    binding.mypageProfileFriendNumTv.setText(userData.roomCollectionList.size.toString())
                    binding.mypageProfileKeyword1.setText("키워드" + userData.keyword1)
                    binding.mypageProfileKeyword2.setText("키워드" + userData.keyword2)
                    binding.mypageProfileKeyword3.setText("키워드" + userData.keyword3)
                    // 매칭 친구 수 todo 백엔드 API 수정 필요
                    viewFriendList(1.toLong()) // todo 추후에 삭제
                } else {
                    Toast.makeText(context, "프로필 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                //Toast.makeText(context, "서버 연결 실패", Toast.LENGTH_SHORT).show()
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
                binding.mypageProfileFriendNumTv.setText(friendList.size.toString())
            }

            override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                Log.d("retrofit", "친구 목록 - 응답 실패 / t: $t")
            }
        })
    }
}