package com.fine_app.ui.Community

import android.content.Intent
import android.os.Bundle
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
import com.fine_app.R

class CommunityMainFragment : Fragment() {

    //private var _binding: FragmentCommunityMainBinding? = null
    //private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val postViewModel: PostViewModel by lazy{
        ViewModelProvider(this).get(PostViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        /*_binding = FragmentCommunityMainBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val currentFragment=childFragmentManager.findFragmentById(binding.fragmentContainer.id)
        if(currentFragment==null){
            childFragmentManager.beginTransaction()
                .add(R.id.fragment_container, PostFragment_Main())
                .commit()
        }
        return root
        */
        val view=inflater.inflate(R.layout.fragment_community_main, container, false)
        recyclerView=view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager= LinearLayoutManager(context)

        val posts=postViewModel.postList
        var adapter=MyAdapter(posts)
        adapter.filter.filter("1")
        recyclerView.adapter=adapter

        return view
    }
    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        private lateinit var post:Post
        private val list_title: TextView =itemView.findViewById(R.id.mainpost_title)
        private val list_comment: TextView =itemView.findViewById(R.id.mainpost_comment)
        init{
            list_title.setOnClickListener(this)
        }
        fun bind(post: Post){
            this.post=post
            list_title.text=this.post.title
            list_comment.text=this.post.comment
        }

        override fun onClick(p0: View?) {
            var PostDetail= Intent(getActivity(), PostDetail_Main::class.java)
            PostDetail.putExtra("nickname", post.nickname)
            PostDetail.putExtra("profileID", post.profileID)
            PostDetail.putExtra("title", post.title)
            PostDetail.putExtra("content", post.content)
            PostDetail.putExtra("capacity", post.capacity)
            PostDetail.putExtra("participants", post.participants)
            startActivity(PostDetail)
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
                            if (item.capacity == charString) filteringList.add(item)
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

    //override fun onDestroyView() {
    //   super.onDestroyView()
    //    _binding = null
    //}

}