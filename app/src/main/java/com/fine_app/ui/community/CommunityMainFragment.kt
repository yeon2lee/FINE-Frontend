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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.R
import com.fine_app.databinding.FragmentCommunityMainBinding
import com.fine_app.databinding.ItemPostlistMainBinding

class CommunityMainFragment : Fragment() {

    private var _binding: FragmentCommunityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var postModel:PostModel
    //private lateinit var recyclerView: RecyclerView
    /*
    private val postViewModel: PostViewModel by lazy{
        ViewModelProvider(this).get(PostViewModel::class.java)
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Log.d("test", "일반 시작")
        _binding = FragmentCommunityMainBinding.inflate(inflater, container, false)
        val root: View = binding.root
        postModel= PostModel()

        //val view=inflater.inflate(R.layout.fragment_community_main, container, false)
        //recyclerView=view.findViewById(R.id.recyclerView)
        //recyclerView.layoutManager= LinearLayoutManager(context)

        /*필터 테스트
        val posts=postModel.posts
        var adapter=MyAdapter(posts)
        adapter.filter.filter("1")


         */
/*
        //포스팅 정보받아오기
        val nickname = requireArguments().getString("nickname")
        val image = requireArguments().getInt("image")
        val title = requireArguments().getString("title")
        val content = requireArguments().getString("content")
        val capacity = requireArguments().getString("capacity")
        val postviewmodel=PostViewModel()
        postviewmodel.post=Post(nickname.toString(), image,title.toString(), content.toString(), "0", "0", capacity.toString())
*/
        //recyclerView.adapter=adapter
        binding.recyclerView.apply{
            layoutManager=LinearLayoutManager(context)
            adapter=MyAdapter(postModel.posts)
        }

        return root
        //return view
    }
    inner class MyViewHolder(private val binding:ItemPostlistMainBinding): RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        init {binding.viewModel=PostViewModel() }

        private lateinit var post:Post
        //private val list_title: TextView =itemView.findViewById(R.id.mainpost_title)
        private val list_title:TextView=binding.mainpostTitle
        //private val list_comment: TextView =itemView.findViewById(R.id.mainpost_comment)
        private val list_comment: TextView =binding.mainpostComment
        init{
            list_title.setOnClickListener(this)
        }
        fun bind(post: Post) {
            binding.apply {
                viewModel?.post = post
                executePendingBindings()
            }
        }
        override fun onClick(p0: View?) {
            var PostDetail= Intent(getActivity(), PostDetail_Main::class.java)
            PostDetail.putExtra("nickname", binding.viewModel?.nickname)
            PostDetail.putExtra("profileID",binding.viewModel?.profileID)
            PostDetail.putExtra("title", binding.viewModel?.title)
            PostDetail.putExtra("content", binding.viewModel?.content)
            PostDetail.putExtra("capacity", binding.viewModel?.capacity)
            PostDetail.putExtra("participants", binding.viewModel?.participants)
            startActivity(PostDetail)
        }
    }
    inner class MyAdapter(private val posts:List<Post>): RecyclerView.Adapter<MyViewHolder>(){ //, Filterable
/*
        val unFilteredList = posts //⑴
        var filteredList = posts

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



 */
        override fun getItemCount(): Int = posts.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            //val view=layoutInflater.inflate(R.layout.item_postlist_main, parent, false)
            //return MyViewHolder(view)
            val binding= DataBindingUtil.inflate<ItemPostlistMainBinding>(layoutInflater,R.layout.item_postlist_main, parent, false )
            return MyViewHolder(binding)
            }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            /*
            val post=filteredList[position]
            holder.bind(post)

             */
            val post=posts[position]
            holder.bind(post)
        }
    }
}


