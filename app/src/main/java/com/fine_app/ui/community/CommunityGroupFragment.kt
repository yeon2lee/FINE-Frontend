package com.fine_app.ui.community

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Post
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
        recyclerView=binding.recyclerView
        recyclerView.layoutManager= LinearLayoutManager(context)
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


/*
        val posts=postViewModel.postList
        var adapter=MyAdapter(posts)

        val buttonList:RadioGroup=binding.radioGroup
        adapter.filter.filter("all")
        buttonList.setOnCheckedChangeListener{_, checkedId ->
            when(checkedId){
                R.id.radioButton_all -> adapter.filter.filter("all")
                R.id.radioButton_finished -> adapter.filter.filter("finished")
                R.id.radioButton_ongoing -> adapter.filter.filter("ongoing")
            }
        }
        recyclerView.adapter=adapter

 */

        return root
    }

    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view), View.OnClickListener{
        private lateinit var post: Post
        private val postTitle: TextView =itemView.findViewById(R.id.groupPost_title)
        private val participant: TextView =itemView.findViewById(R.id.groupPost_participant)
        private val capacity: TextView =itemView.findViewById(R.id.groupPost_capacity)
        private val partition: TextView=itemView.findViewById(R.id.groupPost_partition)
        private val image:ImageView=itemView.findViewById(R.id.groupPost_imageView)
        init{
            postTitle.setOnClickListener(this)
        }
        fun bind(post: Post){
            this.post=post
            postTitle.text=this.post.title

            if(this.post.closingCheck){
                participant.text=""
                capacity.text=""
                partition.text="모집 완료"
                image.visibility=View.INVISIBLE
            }else{
                //participant.text=this.post.participants
                capacity.text=this.post.capacity.toString()
                partition.text="/"
                image.visibility=View.VISIBLE
            }
        }

        override fun onClick(p0: View?) {
            viewGroupPosting("postingID")
        }
    }
    inner class MyAdapter(private val list:List<Post>): RecyclerView.Adapter<MyViewHolder>(){//, Filterable
/*
        val unFilteredList = list
        var returnList=list

        override fun getItemCount(): Int = returnList.size
        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val charString = constraint.toString()
                    if (charString.isEmpty()) {
                        returnList = unFilteredList
                    } else {
                        var ongoingList = ArrayList<Post>()
                        var finishedList=ArrayList<Post>()
                        var groupList=ArrayList<Post>()
                        for (item in unFilteredList) {
                            if(item.groupCheck==true) {
                                groupList.add(item)
                                if (item.closingCheck) finishedList.add(item)  //모집완료
                                else ongoingList.add(item) //모집중
                            }
                        }
                        when (charString) {
                            "ongoing" -> returnList = ongoingList
                            "finished" -> returnList = finishedList
                            "all" -> returnList=groupList
                        }
                    }
                    val filterResults = FilterResults()
                    filterResults.values = returnList
                    return filterResults
                }
                override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                    returnList = results.values as ArrayList<Post>
                    notifyDataSetChanged()
                }
            }
        }

 */

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_postlist_group, parent, false)
            return MyViewHolder(view)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            //val post=returnList[position]
            val post=list[position]
            holder.bind(post)
        }
    }
    private fun viewGroupCommunity(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewGroupCommunity() ?:return
        call.enqueue(object : retrofit2.Callback<List<Post>>{
            //응답성공
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("retrofit", "그룹커뮤니티목록 - 응답 성공 / t : ${response.raw()}")
                var adapter=MyAdapter(response.body()!!)
                recyclerView.adapter=adapter
            }
            //응답실패
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("retrofit", "그룹커뮤니티목록 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewGroupCommunityProceed(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewGroupCommunityProceed() ?:return
        call.enqueue(object : retrofit2.Callback<List<Post>>{
            //응답성공
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("retrofit", "그룹커뮤니티 진행 목록 - 응답 성공 / t : ${response.raw()}")
                var adapter=MyAdapter(response.body()!!)
                recyclerView.adapter=adapter
            }
            //응답실패
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("retrofit", "그룹커뮤니티진행 목록 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewGroupCommunityClose(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewGroupCommunityClose() ?:return
        call.enqueue(object : retrofit2.Callback<List<Post>>{
            //응답성공
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("retrofit", "그룹커뮤니티 완료 목록 - 응답 성공 / t : ${response.raw()}")
                var adapter=MyAdapter(response.body()!!)
                recyclerView.adapter=adapter
            }
            //응답실패
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("retrofit", "그룹커뮤니티 완료 목록 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewGroupPosting(postingId:String?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:String= postingId ?:""
        val call = iRetrofit?.viewGroupPosting(PostingID = term) ?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : retrofit2.Callback<Post>{
            //응답성공
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "그룹 커뮤니티 세부 글 - 응답 성공 / t : ${response.raw()}")
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "그룹 커뮤니티 세부 글 - 응답 실패 / t: $t")
            }

        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}