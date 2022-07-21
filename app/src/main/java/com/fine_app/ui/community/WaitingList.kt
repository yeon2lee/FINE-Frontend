package com.fine_app.ui.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.*
import com.fine_app.databinding.CommunityWaitinglistBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WaitingList : AppCompatActivity() {
    private lateinit var binding: CommunityWaitinglistBinding
    private lateinit var recyclerView: RecyclerView
    private val postingID=intent.getLongExtra("postingID", 0)
    private val recruitingList=intent.getSerializableExtra("recruitingList") as ArrayList<Recruit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunityWaitinglistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView=binding.recyclerView
        recyclerView.layoutManager= LinearLayoutManager(this)
        val adapter=MyAdapter(recruitingList)
        recyclerView.adapter=adapter
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var crew: Recruit
        private val image: ImageView =itemView.findViewById(R.id.writer_image)
        private val name: TextView =itemView.findViewById(R.id.writer_name)
        private val level: ImageView =itemView.findViewById(R.id.levelImage)
        private val accept: Button =itemView.findViewById(R.id.acceptButton)

        fun bind(crew: Recruit, position: Int) {
            this.crew=crew
            name.text=this.crew.nickname
            //image.setImageResource(this.crew.capacity)
            //level.setImageResource(this.crew.capacity)
            accept.setOnClickListener{
                acceptJoinGroup(postingID, recruitingList[position].id)
            }
            image.setOnClickListener{
                val userProfile = Intent(this@WaitingList, ShowUserProfileActivity::class.java)
                userProfile.putExtra("memberId",this.crew.memberId )
                startActivity(userProfile)
            }
        }
    }
    inner class MyAdapter(val list:List<Recruit> ): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_waitlist, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val crew=list[position]
            holder.bind(crew, position)
        }
    }

    //------------------------------API 연결------------------------------------

    private fun acceptJoinGroup(postingId:Long?, recruitingId:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= postingId ?:0
        val call = iRetrofit?.acceptJoinGroup(postingId = term, recruitingId=recruitingId, true) ?:return

        call.enqueue(object : Callback<Join> {
            override fun onResponse(call: Call<Join>, response: Response<Join>) {
                Log.d("retrofit", "참여 수락 - 응답 성공 / t : ${response.raw()}")
            }
            override fun onFailure(call: Call<Join>, t: Throwable) {
                Log.d("retrofit", "참여 수락 - 응답 실패 / t: $t")
            }
        })
    }
}