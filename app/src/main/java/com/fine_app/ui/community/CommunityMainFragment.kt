package com.fine_app.ui.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Post
import com.fine_app.R
import com.fine_app.databinding.FragmentCommunityMainBinding
import com.fine_app.retrofit.RetrofitManager

class CommunityMainFragment : Fragment() {

    private var _binding: FragmentCommunityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    private val postViewModel: PostViewModel by lazy{
        ViewModelProvider(this).get(PostViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCommunityMainBinding.inflate(inflater, container, false)
        val root: View = binding.root
        RetrofitManager.instance.viewMainCommunity()

        recyclerView=binding.recyclerView
        recyclerView.layoutManager= LinearLayoutManager(context)

        val posts=postViewModel.postList
        var adapter=MyAdapter(posts)
        adapter.filter.filter("1")
        recyclerView.adapter=adapter

        return root

    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view), View.OnClickListener{

        private lateinit var post: Post
        private val postTitle: TextView =itemView.findViewById(R.id.mainpost_title)
        private val commentNum: TextView =itemView.findViewById(R.id.mainpost_comment)
        init{
            postTitle.setOnClickListener(this)
        }
        fun bind(post: Post) {
            this.post=post
            postTitle.text=this.post.title
            commentNum.text=this.post.commentCount
        }
        override fun onClick(p0: View?) {
            //API 호출
            RetrofitManager.instance.viewMainPosting("1", completion = {
                it ->
                Log.d("retrofit", "메인 글 세부 API 호출 // $it ")
            })

            var postDetail= Intent(getActivity(), PostDetail_Main::class.java)
            postDetail.putExtra("nickname", post.nickname)
            //postDetail.putExtra("profileID", post.profileID)
            postDetail.putExtra("title", post.title)
            postDetail.putExtra("content", post.content)
            postDetail.putExtra("capacity", post.capacity)
            //postDetail.putExtra("participants", post.participants)
            startActivity(postDetail)

        }
    }
    inner class MyAdapter(var list:List<Post>): RecyclerView.Adapter<MyViewHolder>(), Filterable {

        val unFilteredList = list //⑴
        var filteredList = list

        override fun getItemCount(): Int = filteredList.size
        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val charString = constraint.toString()
                    filteredList = if (charString.isEmpty()) { //⑶
                        unFilteredList
                    } else {
                        var filteringList = ArrayList<Post>()
                        for (item in unFilteredList) {
                            if (item.groupCheck==false) filteringList.add(item) //그룹 글이 아니면 추가
                        }
                        filteringList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = filteredList
                    return filterResults
                }
                override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                    filteredList = results?.values as ArrayList<Post>
                    notifyDataSetChanged()
                }
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            val view=layoutInflater.inflate(R.layout.item_postlist_main, parent, false)
            return MyViewHolder(view)
            }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=filteredList[position]
            holder.bind(post)
        }
    }
}


