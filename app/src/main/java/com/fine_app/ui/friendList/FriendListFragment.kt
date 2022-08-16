package com.fine_app.ui.friendList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Friend
import com.fine_app.Member
import com.fine_app.R
import com.fine_app.databinding.FragmentFriendlistBinding
import com.fine_app.databinding.FriendSearchBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.myPage.ManagePostActivity
import com.fine_app.ui.myPage.MyPageFragment
import com.fine_app.ui.myPage.Profile
import com.fine_app.ui.myPage.ServiceCreator
import com.fine_app.ui.community.PostDetail_Group
import com.fine_app.ui.community.ShowUserProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendListFragment : Fragment() {
    private var _binding: FragmentFriendlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val myId:Long=2 // TODO: 내 아이디 불러오기

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFriendlistBinding.inflate(inflater, container, false)
        getMyProfile(myId)
        viewFriendList(myId)
        binding.myImage.setOnClickListener {
            activity?.let{
                val intent = Intent(context, ShowUserProfileActivity::class.java)
                startActivity(intent)
            }
        }
        binding.findFriendButton.setOnClickListener {
            val intent = Intent(context, SearchFriendList::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){
        private lateinit var friend: Friend
        private val friendProfileImage:ImageView=itemView.findViewById(R.id.friend_image)
        private val friendLevelImage:ImageView=itemView.findViewById(R.id.friend_level) //todo 레벨 이미지 정해야함
        private val friendName:TextView=itemView.findViewById(R.id.friend_name)
        private val friendIntro:TextView=itemView.findViewById(R.id.friend_intro)

        fun bind(friend: Friend){
            this.friend=friend
            friendName.text=this.friend.nickname
            friendIntro.text=this.friend.intro

            when (this.friend.imageNum) {
                0 -> friendProfileImage.setImageResource(R.drawable.profile)
                1 -> friendProfileImage.setImageResource(R.drawable.profile1)
                2 -> friendProfileImage.setImageResource(R.drawable.profile2)
                3 -> friendProfileImage.setImageResource(R.drawable.profile3)
                4 -> friendProfileImage.setImageResource(R.drawable.profile4)
                5 -> friendProfileImage.setImageResource(R.drawable.profile5)
                6 -> friendProfileImage.setImageResource(R.drawable.profile6)
                else -> friendProfileImage.setImageResource(R.drawable.profile)
            }
            // todo 사용자 레벨
            friendLevelImage.setImageResource(R.drawable.ic_sprout)

            itemView.setOnClickListener{
                val userProfile = Intent(activity, ShowUserProfileActivity::class.java)
                userProfile.putExtra("memberId",this.friend.friendId)
                startActivity(userProfile)
            }
        }
    }
    inner class MyAdapter(private val list:List<Friend>): RecyclerView.Adapter<MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_friendlist, parent, false)
            return MyViewHolder(view)
        }
        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val friend=list[position]
            holder.bind(friend)
        }
    }

    private fun getMyProfile(myID:Long){
        val call: Call<Profile> = ServiceCreator.service.getMyProfile(myID)

        call.enqueue(object : Callback<Profile>{

            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                Log.d("retrofit", "내 정보 - 응답 성공 / t : ${response.body().toString()}")
                if (response.body() != null) {
                    binding.myName.text=response.body()!!.nickname
                    binding.myIntro.text=response.body()!!.intro
                    var imageResource = response.body()!!.userImageNum
                    when (imageResource) {
                        0 -> binding.myImage.setImageResource(R.drawable.profile)
                        1 -> binding.myImage.setImageResource(R.drawable.profile1)
                        2 -> binding.myImage.setImageResource(R.drawable.profile2)
                        3 -> binding.myImage.setImageResource(R.drawable.profile3)
                        4 -> binding.myImage.setImageResource(R.drawable.profile4)
                        5 -> binding.myImage.setImageResource(R.drawable.profile5)
                        6 -> binding.myImage.setImageResource(R.drawable.profile6)
                        else -> binding.myImage.setImageResource(R.drawable.profile)
                    }
                    //todo 레벨 연결
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Log.d("retrofit", "내 정보 - 응답 실패 / t: $t")
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
                val adapter=MyAdapter(response.body()!!)
                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(context)
                recyclerView.adapter=adapter
            }

            override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                Log.d("retrofit", "친구 목록 - 응답 실패 / t: $t")
            }
        })
    }
    override fun onResume() {
        super.onResume()
        viewFriendList(myId)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}