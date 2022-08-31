package com.fine_app.ui.friendList

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.*
import com.fine_app.databinding.FriendSearchBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.community.ShowUserProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class SearchFriendList : AppCompatActivity() {
    private lateinit var binding: FriendSearchBinding
    private lateinit var recyclerView: RecyclerView
    private var myID by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)
        myID = userInfo.getString("userInfo", "2")!!.toLong()

        binding = FriendSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {
                searchFriend(p0)
                return true
            }
            override fun onQueryTextChange(p0: String): Boolean {
                return true
            }
        })
        binding.cancelButton.setOnClickListener{
            finish()
        }

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

            itemView.setOnClickListener{
                val userProfile = Intent(this@SearchFriendList, ShowUserProfileActivity::class.java)
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

    //------------------------------API 연결------------------------------------

    private fun searchFriend(search:String){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.searchFriend(memberId = myID, search = search) ?:return

        call.enqueue(object : Callback<List<Friend>> {
            override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                Log.d("retrofit", "친구 검색 - 응답 성공 / t : ${response.body().toString()}")
                if(response.body()!=null){
                    val adapter=MyAdapter(response.body()!!)
                    recyclerView=binding.recyclerView
                    recyclerView.layoutManager= LinearLayoutManager(this@SearchFriendList)
                    recyclerView.adapter=adapter
                }
            }
            override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                Log.d("retrofit", "친구 검색 - 응답 실패 / t: $t")
            }
        })
    }

}