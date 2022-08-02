package com.fine_app.ui.community

import android.content.Intent
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
import com.fine_app.databinding.CommunitySearchBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchList : AppCompatActivity() {
    private lateinit var binding: CommunitySearchBinding
    private lateinit var recyclerView: RecyclerView
    private val myID:Long=1 //todo 내 id 가져오기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val text=intent.getStringExtra("text")
        if (text != "") {
            binding.searchView.setQuery(text, false)
            searchPosting(text!!)
        }
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            //검색 버튼 눌렀을 때 호출
            override fun onQueryTextSubmit(p0: String): Boolean {
                searchPosting(p0)
                return true
            }
            //텍스트 입력과 동시에 호출
            override fun onQueryTextChange(p0: String): Boolean {
                return true
            }
        })
        binding.cancelButton.setOnClickListener{
            finish()
        }

    }
    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var post: Post
        private val title: TextView = itemView.findViewById(R.id.searchTitle)!!
        //private val content: TextView =itemView.findViewById(R.id.searchContent)!!
        private val groupCheck: TextView =itemView.findViewById(R.id.searchGroup)!!
        private val commentNum: TextView =itemView.findViewById(R.id.searchComment)!!
        private val writtenTime: TextView =itemView.findViewById(R.id.searchDate)!!
        private val commentImage: ImageView =itemView.findViewById(R.id.imageView13 )!!

        fun bind(post: Post) {
            this.post=post
            //content.text=this.post.content
            title.text=this.post.title
            val token=this.post.createdDate.split("-", "T", ":")
            writtenTime.text=token[1]+"/"+token[2]+" "+token[3]+":"+token[4]
            if(this.post.closingCheck) {
                commentImage.visibility=View.INVISIBLE
                commentNum.text="마감"
            }
            else {
                commentImage.visibility=View.VISIBLE
                commentNum.text=this.post.commentCount
            }

            if (this.post.groupCheck){ //그룹포스트
                groupCheck.text="그룹 커뮤니티"
            }else{
                groupCheck.text="일반 커뮤니티"
            }

            itemView.setOnClickListener{
                if (this.post.groupCheck){ //그룹포스트
                    viewGroupPosting(this.post.postingId, myID)

                }else{
                    viewMainPosting(this.post.postingId, myID)
                }
            }
        }
    }
    inner class MyAdapter(val list:List<Post>): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_search, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }

    //------------------------------API 연결------------------------------------

    private fun searchPosting(title:String){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.searchPosting(title = title) ?:return

        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("retrofit", "커뮤니티 글 검색 - 응답 성공 / t : ${response.body().toString()}")
                val adapter=MyAdapter(response.body()!!)
                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(this@SearchList)
                recyclerView.adapter=adapter
            }
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("retrofit", "커뮤니티 글 검색 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewGroupPosting(postingId:Long?, memberId:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= postingId ?:0
        val call = iRetrofit?.viewPosting(postingId = term, memberId = memberId) ?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : Callback<Post>{
            //응답성공
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "그룹 커뮤니티 세부 글 - 응답 성공 / t : ${response.raw()}")

                val postDetail= Intent(this@SearchList, PostDetail_Group::class.java)
                postDetail.putExtra("nickname", response.body()!!.nickname)
                postDetail.putExtra("title", response.body()!!.title)
                postDetail.putExtra("content", response.body()!!.content)
                postDetail.putExtra("comments", response.body()!!.comments)
                postDetail.putExtra("capacity", response.body()!!.capacity)
                postDetail.putExtra("lastModifiedDate", response.body()!!.lastModifiedDate)
                postDetail.putExtra("closingCheck", response.body()!!.closingCheck)
                postDetail.putExtra("recruitingList", response.body()!!.recruitingList)
                postDetail.putExtra("memberId", response.body()!!.memberId)
                postDetail.putExtra("postingId", postingId)
                startActivity(postDetail)
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "그룹 커뮤니티 세부 글 - 응답 실패 / t: $t")
            }

        })
    }
    private fun viewMainPosting(postingId:Long?, memberId:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= postingId ?:0
        val call = iRetrofit?.viewPosting(postingId = term, memberId=memberId) ?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : Callback<Post>{
            //응답성공
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "메인 커뮤니티 세부 글 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", response.body().toString())

                val postDetail= Intent(this@SearchList, PostDetail_Main::class.java)
                postDetail.putExtra("nickname", response.body()!!.nickname)
                postDetail.putExtra("title", response.body()?.title)
                postDetail.putExtra("content", response.body()?.content)
                postDetail.putExtra("comments", response.body()?.comments)
                postDetail.putExtra("capacity", response.body()?.capacity)
                postDetail.putExtra("createdDate", response.body()?.createdDate)
                postDetail.putExtra("lastModifiedDate", response.body()?.lastModifiedDate)
                postDetail.putExtra("memberId", response.body()?.memberId)
                postDetail.putExtra("postingId", postingId)
                startActivity(postDetail)
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "메인 커뮤니티 세부 글 - 응답 실패 / t: $t")
            }
        })
    }
}