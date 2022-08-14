package com.fine_app.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.*
import com.fine_app.databinding.FragmentHomeBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.community.PostDetail_Main
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewPopularPosting()

        binding.showButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_home_recommend)
        }
        return root
    }

    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){

        private lateinit var post: Post
        private val recommendTitle:TextView =itemView.findViewById(R.id.recommendTitle)
        private val recommendContent: TextView =itemView.findViewById(R.id.recommendContent)
        private val recommendCapacity: TextView =itemView.findViewById(R.id.recommendCapacity)
        private val recommendVacancy: TextView =itemView.findViewById(R.id.recommendVacancy)
        //private val recommendKeyWord3: TextView =itemView.findViewById(R.id.recommendKeyWord3)
        //private val recommendKeyWord2: TextView =itemView.findViewById(R.id.recommendKeyWord2)
        //private val recommendKeyWord1: TextView =itemView.findViewById(R.id.recommendKeyWord1)

        fun bind(post: Post) {
            this.post=post
            recommendTitle.text=this.post.title
            recommendContent.text=this.post.content
            recommendCapacity.text=this.post.capacity.toString()
            recommendVacancy.text=(this.post.capacity - this.post.participants).toString()

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

            val view=layoutInflater.inflate(R.layout.item_recommend, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }
    private fun viewPopularPosting(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewPopularPosting() ?:return

        call.enqueue(object : Callback<List<Post>> {

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("retrofit", "친구 목록 - 응답 성공 / t : ${response.raw()}")
                recyclerView=binding.recyclerView
                recyclerView.layoutManager=LinearLayoutManager(context ,LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter=MyAdapter(response.body()!!)
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("retrofit", "친구 목록 - 응답 실패 / t: $t")
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}