package com.fine_app.ui.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Post
import com.fine_app.R
import com.fine_app.databinding.FragmentCommunityMainBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class CommunityMainFragment : Fragment() {

    private var _binding: FragmentCommunityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityMainBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewMainCommunity()
        return root
    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){

        private lateinit var post: Post
        private val postTitle: TextView =itemView.findViewById(R.id.mainpost_title)
        private val commentNum: TextView =itemView.findViewById(R.id.mainpost_comment)

        fun bind(post: Post) {
            this.post=post
            postTitle.text=this.post.title
            commentNum.text=this.post.commentCount
            itemView.setOnClickListener{
                val postDetail= Intent(activity, PostDetail_Main::class.java)
                postDetail.putExtra("postingId", this.post.postingId)
                postDetail.putExtra("memberId", this.post.memberId)
                startActivity(postDetail)
            }
        }
    }
    inner class MyAdapter(val list:List<Post>): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            val view=layoutInflater.inflate(R.layout.item_postlist_main, parent, false)
            return MyViewHolder(view)
            }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }

    private fun viewMainCommunity(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewMainCommunity() ?:return

        call.enqueue(object : retrofit2.Callback<List<Post>>{
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("retrofit", "메인커뮤니티목록 - 응답 성공 / t : ${response.raw()}")
                val adapter=MyAdapter(response.body()!!)
                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(context)
                recyclerView.adapter=adapter
            }
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("retrofit", "메인커뮤니티목록 - 응답 실패 / t: $t")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewMainCommunity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


