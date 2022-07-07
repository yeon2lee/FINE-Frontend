package com.fine_app.ui.Community

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.R


class CommunityGroupFragment : Fragment() {
    //private var _binding: FragmentCommunityGroupBinding? = null
    //private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val postViewModel: PostViewModel by lazy{
        ViewModelProvider(this).get(PostViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        /*
        _binding = FragmentCommunityGroupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val currentFragment=childFragmentManager.findFragmentById(binding.fragmentContainer.id)
        if(currentFragment==null){
            childFragmentManager.beginTransaction()
                .add(R.id.fragment_container, PostFragment_Group())
                .commit()

        }
        return root

         */


        val view=inflater.inflate(R.layout.fragment_community_group, container, false)



        recyclerView=view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager= LinearLayoutManager(context)

        val posts=postViewModel.postList
        val adapter=MyAdapter(posts)
        val buttonList:RadioGroup=view.findViewById(R.id.radioGroup)
        adapter.filter.filter("all")
        buttonList.setOnCheckedChangeListener{_, checkedId ->
            when(checkedId){
                R.id.radioButton_all -> adapter.filter.filter("all")
                R.id.radioButton_finished -> adapter.filter.filter("finished")
                R.id.radioButton_ongoing -> adapter.filter.filter("ongoing")
            }
        }
        recyclerView.adapter=adapter
        return view
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        private lateinit var post:Post
        private val title: TextView =itemView.findViewById(R.id.groupPost_title)
        private val participant: TextView =itemView.findViewById(R.id.groupPost_participant)
        private val capacity: TextView =itemView.findViewById(R.id.groupPost_capacity)
        private val partition: TextView=itemView.findViewById(R.id.groupPost_partition)
        private val image:ImageView=itemView.findViewById(R.id.groupPost_imageView)
        init{
            title.setOnClickListener(this)
        }
        fun bind(post: Post){
            this.post=post
            title.text=this.post.title

            if(this.post.participants==this.post.capacity){
                participant.text=""
                capacity.text=""
                partition.text="모집 완료"
                image.visibility=View.INVISIBLE
            }else{
                participant.text=this.post.participants
                capacity.text=this.post.capacity
                partition.text="/"
                image.visibility=View.VISIBLE
            }
        }

        override fun onClick(p0: View?) {
            val postDetail= Intent(getActivity(), PostDetail_Main::class.java)
            postDetail.putExtra("title", post.title)
            postDetail.putExtra("content", post.content)
            postDetail.putExtra("capacity", post.capacity)
            postDetail.putExtra("participants", post.participants)
            startActivity(postDetail)
        }
    }
    inner class MyAdapter(var list:List<Post>): RecyclerView.Adapter<MyViewHolder>(), Filterable {
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
                            if(item.capacity !="1") {
                                groupList.add(item)
                                if (item.capacity == item.participants) finishedList.add(item)  //모집완료
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


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_postlist_group, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=returnList[position]
            holder.bind(post)
        }
    }
    //override fun onDestroyView() {
    //    super.onDestroyView()
    //    _binding = null
    //}

}