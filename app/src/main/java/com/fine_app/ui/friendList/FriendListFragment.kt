package com.fine_app.ui.friendList

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Friend
import com.fine_app.R
import com.fine_app.databinding.FragmentFriendlistBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.myPage.Profile
import com.fine_app.ui.myPage.ServiceCreator
import com.fine_app.ui.community.ShowUserProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class FriendListFragment : Fragment() {
    private var _binding: FragmentFriendlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private var myId by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFriendlistBinding.inflate(inflater, container, false)
        userInfo = this.requireActivity().getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
        myId = userInfo.getString("userInfo", "2")!!.toLong()

        getMyProfile(myId)
        viewFriendList(myId)
        binding.myImage.setOnClickListener {
            activity?.let{
                val intent = Intent(context, ShowUserProfileActivity::class.java)
                intent.putExtra("memberId", myId)
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
                0 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                1 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                2 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                3 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                4 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                5 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                6 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                else -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
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
                        0 -> binding.myImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        1 -> binding.myImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        2 -> binding.myImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                        3 -> binding.myImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                        4 -> binding.myImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                        5 -> binding.myImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                        6 -> binding.myImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                        else -> binding.myImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
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
        myId = userInfo.getString("userInfo", "2")!!.toLong()
        viewFriendList(myId)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}