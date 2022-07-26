package com.fine_app.ui.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.GroupPost
import com.fine_app.R
import com.fine_app.databinding.FragmentCommunityGroupBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response


class CommunityGroupFragment : Fragment() {
    private var _binding: FragmentCommunityGroupBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCommunityGroupBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val buttonList:RadioGroup=binding.radioGroup
        buttonList.setOnCheckedChangeListener{_, checkedId ->
            when(checkedId){
                R.id.radioButton_all -> viewGroupCommunity()
                R.id.radioButton_finished -> viewGroupCommunityClose()
                R.id.radioButton_ongoing -> viewGroupCommunityProceed()
            }
        }
        // API 호출
        viewGroupCommunity()
        return root
    }

    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){ //, View.OnClickListener
        private lateinit var post: GroupPost
        private val postTitle: TextView =itemView.findViewById(R.id.groupPost_title)
        private val participant: TextView =itemView.findViewById(R.id.groupPost_participant)
        private val capacity: TextView =itemView.findViewById(R.id.groupPost_capacity)
        private val partition: TextView=itemView.findViewById(R.id.groupPost_partition)
        private val image:ImageView=itemView.findViewById(R.id.groupPost_imageView)

        fun bind(post: GroupPost){
            this.post=post
            postTitle.text=this.post.title

            if(this.post.closingCheck){
                participant.text=""
                capacity.text=""
                partition.text="모집 완료"
                image.visibility=View.INVISIBLE
            }else{
                participant.text= this.post.participants.toString()
                capacity.text=this.post.capacity.toString()
                partition.text="/"
                image.visibility=View.VISIBLE
            }
            itemView.setOnClickListener{
                viewGroupPosting(this.post.postingId)
            }
        }

    }
    inner class MyAdapter(private val list:List<GroupPost>): RecyclerView.Adapter<MyViewHolder>(){//, Filterable
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_postlist_group, parent, false)
            return MyViewHolder(view)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }
    private fun viewGroupCommunity(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewGroupCommunity() ?:return
        call.enqueue(object : retrofit2.Callback<List<GroupPost>>{
            //응답성공
            override fun onResponse(call: Call<List<GroupPost>>, response: Response<List<GroupPost>>) {
                Log.d("retrofit", "그룹커뮤니티목록 - 응답 성공 / t : ${response.raw()}")
                val adapter=MyAdapter(response.body()!!)
                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(context)
                recyclerView.adapter=adapter
            }
            //응답실패
            override fun onFailure(call: Call<List<GroupPost>>, t: Throwable) {
                Log.d("retrofit", "그룹커뮤니티목록 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewGroupCommunityProceed(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewGroupCommunityProceed() ?:return
        call.enqueue(object : retrofit2.Callback<List<GroupPost>>{
            //응답성공
            override fun onResponse(call: Call<List<GroupPost>>, response: Response<List<GroupPost>>) {
                Log.d("retrofit", "그룹커뮤니티 진행 목록 - 응답 성공 / t : ${response.raw()}")
                val adapter=MyAdapter(response.body()!!)
                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(context)
                recyclerView.adapter=adapter
            }
            //응답실패
            override fun onFailure(call: Call<List<GroupPost>>, t: Throwable) {
                Log.d("retrofit", "그룹커뮤니티진행 목록 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewGroupCommunityClose(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewGroupCommunityClose() ?:return
        call.enqueue(object : retrofit2.Callback<List<GroupPost>>{
            //응답성공
            override fun onResponse(call: Call<List<GroupPost>>, response: Response<List<GroupPost>>) {
                Log.d("retrofit", "그룹커뮤니티 완료 목록 - 응답 성공 / t : ${response.raw()}")
                val adapter=MyAdapter(response.body()!!)
                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(context)
                recyclerView.adapter=adapter
            }
            //응답실패
            override fun onFailure(call: Call<List<GroupPost>>, t: Throwable) {
                Log.d("retrofit", "그룹커뮤니티 완료 목록 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewGroupPosting(postingId:Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= postingId ?:0
        val call = iRetrofit?.viewGroupPosting(postingId = term) ?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : retrofit2.Callback<GroupPost>{
            //응답성공
            override fun onResponse(call: Call<GroupPost>, response: Response<GroupPost>) {
                Log.d("retrofit", "그룹 커뮤니티 세부 글 - 응답 성공 / t : ${response.raw()}")

                val postDetail= Intent(activity, PostDetail_Group::class.java)
                postDetail.putExtra("nickname", response.body()!!.nickname)
                postDetail.putExtra("title", response.body()!!.title)
                postDetail.putExtra("content", response.body()!!.content)
                postDetail.putExtra("comments", response.body()!!.comments)
                postDetail.putExtra("capacity", response.body()!!.capacity)
                postDetail.putExtra("participants", response.body()!!.participants)
                postDetail.putExtra("lastModifiedDate", response.body()!!.lastModifiedDate)
                postDetail.putExtra("closingCheck", response.body()!!.closingCheck)
                postDetail.putExtra("recruitingList", response.body()!!.recruitingList)
                postDetail.putExtra("memberId", response.body()!!.memberId)
                postDetail.putExtra("postingId", postingId)
                startActivity(postDetail)
            }
            //응답실패
            override fun onFailure(call: Call<GroupPost>, t: Throwable) {
                Log.d("retrofit", "그룹 커뮤니티 세부 글 - 응답 실패 / t: $t")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        viewGroupCommunity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}