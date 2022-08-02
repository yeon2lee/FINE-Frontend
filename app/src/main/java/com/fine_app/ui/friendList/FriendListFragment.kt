package com.fine_app.ui.friendList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Friend
import com.fine_app.Member
import com.fine_app.R
import com.fine_app.databinding.FragmentFriendlistBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.MyPage.Profile
import com.fine_app.ui.MyPage.ServiceCreator
import com.fine_app.ui.community.ShowUserProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendListFragment : Fragment() {
    private var _binding: FragmentFriendlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val myId:Long=1 // TODO: 내 아이디 불러오기

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFriendlistBinding.inflate(inflater, container, false)
        getMyProfile(myId)
        viewFriendList(myId)
        return binding.root
    }

    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){
        private lateinit var friend: Friend
        private val friendProfileImage:ImageView=itemView.findViewById(R.id.friend_image) //todo 친구 프로필 이미지
        private val friendLevelImage:ImageView=itemView.findViewById(R.id.friend_level) //todo 레벨 이미지 정해야함
        private val friendName:TextView=itemView.findViewById(R.id.friend_name)
        private val friendIntro:TextView=itemView.findViewById(R.id.friend_intro)

        fun bind(friend: Friend){
            this.friend=friend
            friendName.text=this.friend.nickname
            friendIntro.text=this.friend.intro

            itemView.setOnClickListener{
                val userProfile = Intent(activity, ShowUserProfileActivity::class.java)
                userProfile.putExtra("memberId",this.friend.friendId)
                startActivity(userProfile)
            }
        }
    }
    inner class MyAdapter(private val list:List<Friend>): RecyclerView.Adapter<MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_postlist_group, parent, false)
            return MyViewHolder(view)
        }
        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val friend=list[position]
            holder.bind(friend)
        }
    }

    private fun getMyProfile(myID:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.getMyProfile(memberId=myID) ?:return

        call.enqueue(object : Callback<Member>{

            override fun onResponse(call: Call<Member>, response: Response<Member>) {
                Log.d("retrofit", "내 정보 - 응답 성공 / t : ${response.body().toString()}")
                binding.myName.text=response.body()!!.nickname
                binding.myIntro.text=response.body()!!.intro
                //todo 프로필 사진 연결
                //todo 레벨 연결
            }

            override fun onFailure(call: Call<Member>, t: Throwable) {
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