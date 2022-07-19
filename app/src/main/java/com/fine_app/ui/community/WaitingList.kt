package com.fine_app.ui.community

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Post
import com.fine_app.R
import com.fine_app.databinding.CommunityWaitinglistBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class WaitingList : AppCompatActivity() {
    private lateinit var binding: CommunityWaitinglistBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunityWaitinglistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView=binding.recyclerView
        recyclerView.layoutManager= LinearLayoutManager(this)
        val postingID=intent.getLongExtra("postingID", 0)
        viewWaitingList(postingID)

    }
    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var post: Post
        private val image: ImageView =itemView.findViewById(R.id.writer_image)
        private val name: TextView =itemView.findViewById(R.id.writer_name)
        private val level: ImageView =itemView.findViewById(R.id.levelImage)

        fun bind(post: Post) {
            this.post=post
            name.text=this.post.commentCount
            image.setImageResource(this.post.capacity)
            level.setImageResource(this.post.capacity)
            itemView.setOnClickListener{ //todo 리사이클러뷰 아이템 클릭 작동 확인
            }
        }

    }
    inner class MyAdapter(val list:List<Post> /*todo api 데이터 형태 확인 필요*/ ): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_waitlist_group, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }

    private fun viewWaitingList(postingId:Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= postingId ?:0
        val call = iRetrofit?.viewWaitingList(postingId = term) ?:return

        call.enqueue(object : retrofit2.Callback<List<Post>>{
            //응답성공
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("retrofit", "대기자 목록 - 응답 성공 / t : ${response.raw()}")
                val adapter=MyAdapter(response.body()!!)

                recyclerView.adapter=adapter
            }
            //응답실패
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("retrofit", "대기자 목록 - 응답 실패 / t: $t")
            }
        })
    }
}