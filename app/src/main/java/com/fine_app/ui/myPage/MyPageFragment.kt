package com.fine_app.ui.MyPage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fine_app.R
import com.fine_app.databinding.FragmentMypageBinding
import com.fine_app.ui.myPage.ManageBookmarkActivity
import com.fine_app.ui.myPage.SettingActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    var userId: Long = 0
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
                } else {
                    Toast.makeText(context, "프로필 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                //Toast.makeText(context, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }

        })

    }

}