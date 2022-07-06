package com.fine_app.ui.MyPage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fine_app.MainActivity
import com.fine_app.databinding.FragmentFriendlistBinding
import com.fine_app.databinding.FragmentMypageBinding
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

        binding.btnKeyword.setOnClickListener {
            activity?.let{
                val intent = Intent(context, UpdateProfileActivity::class.java)
                intent.putExtra("nickname", binding.tvNickname.text.toString())
                intent.putExtra("info", binding.tvInfo.text.toString())
                startActivity(intent)
            }
        }

//        binding.tvNickname.setOnClickListener {
//            userId = "1"
//
//            if(userId != "") {
//                callMypageAPI(userId)
//                subscribeViewModel()
//            } else {
//                //Toast.makeText(this, "Github ID를 입력해주세요.", Toast.LENGTH_SHORT).show()
//            }
//        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun callMypageAPI(userId: String) = mypageViewModel.requestUserInfo(userId)
//
//    private fun subscribeViewModel() {
//        mypageViewModel.okCode.observe(viewLifecycleOwner){
//            if(it) {
//                val userData = mypageViewModel.userData
//                if(!userData.id.equals(0)) {
//                    binding.tvNickname.text = userData.nickname
//                    binding.tvInfo.text = userData.userIntroduction
//                } else {
//                    //Toast.makeText(this, "입력하신 ID는 존재하지 않습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
//                }
//
//            } else {
//                //Toast.makeText(this, "입력하신 ID의 상세 정보 조회를 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    private fun getMyProfile() {
        userId = 1

        val call: Call<Profile> = ServiceCreator.service.getMyProfile(userId)

        call.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    userData = response.body()?.let {
                        Profile(
                            id = it.id,
                            nickname = it.nickname,
                            email = it.email,
                            userIntroduction = it.userIntroduction,
                            userUniversity = it.userUniversity,
                            userCollege = it.userCollege,
                            userPhoneNumber = it.userPhoneNumber,
                            userResidence = it.userResidence,
                            level = it.level,
                            report = it.report
                        )
                    } ?: Profile(0, "user", "", "", "", "", "", "", "", 0)

                    binding.tvNickname.setText(userData.nickname)
                    binding.tvInfo.setText(userData.userIntroduction)

                    Toast.makeText(context,"성공", Toast.LENGTH_SHORT).show()
                    val data = response.body()?.nickname
                    Toast.makeText(context, "${data}님 반갑습니다!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "프로필 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Toast.makeText(context, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }

        })

    }

}