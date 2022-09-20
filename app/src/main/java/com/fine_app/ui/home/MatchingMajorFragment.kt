package com.fine_app.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.MatchingFriend
import com.fine_app.R
import com.fine_app.Test
import com.fine_app.databinding.FragmentMatchingBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class MatchingMajorFragment : Fragment() {
    private var _binding: FragmentMatchingBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private var myId by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMatchingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userInfo = this.requireActivity().getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
        myId = userInfo.getString("userInfo", "2")!!.toLong()

        val items = arrayOf("신규순", "신뢰도순")
        binding.spinner3.adapter= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        binding.spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> viewMatchingFriends(1, "new")
                    else -> viewMatchingFriends(1, "level")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        return root
    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){

        private lateinit var friend: MatchingFriend
        private val nickname: TextView =itemView.findViewById(R.id.matching_name)
        private val intro: TextView =itemView.findViewById(R.id.matching_intro)
        private val image: ImageView =itemView.findViewById(R.id.friend_image)
        private val keyword1: TextView =itemView.findViewById(R.id.matching_intro)
        private val keyword2: TextView =itemView.findViewById(R.id.matching_intro)
        private val keyword3: TextView =itemView.findViewById(R.id.matching_intro)
        fun bind(friend: MatchingFriend) {
            this.friend=friend
            keyword1.text=this.friend.keyword1
            keyword2.text=this.friend.keyword2
            keyword3.text=this.friend.keyword3
            nickname.text=this.friend.nickname
            intro.text=this.friend.intro
            when (this.friend.userImageNum) {
                0 -> image.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                1 -> image.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                2 -> image.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                3 -> image.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                4 -> image.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                5 -> image.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                6 -> image.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                else -> image.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
            }
            itemView.setOnClickListener{
            }
        }
    }
    inner class MyAdapter(val list:List<MatchingFriend>): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_matching, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }
    private fun viewMatchingFriends(category:Int,select:String){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewMatchingFriends(myId, category = category, select=select ) ?:return

        call.enqueue(object : Callback<List<MatchingFriend>> {

            override fun onResponse(call: Call<List<MatchingFriend>>, response: Response<List<MatchingFriend>>) {
                Log.d("retrofit", "홈 친구추천 - 응답 성공 / t : ${response.raw()}")
                val adapter=MyAdapter(response.body()!!)
                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(context)
                recyclerView.adapter=adapter
            }

            override fun onFailure(call: Call<List<MatchingFriend>>, t: Throwable) {
                Log.d("retrofit", "홈 친추구천 - 응답 실패 / t: $t")
            }
        })
    }
}