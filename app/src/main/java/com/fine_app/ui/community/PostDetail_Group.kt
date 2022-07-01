package com.fine_app.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.R
import com.fine_app.databinding.CommunityGroupPostBinding

class PostDetail_Group : AppCompatActivity() {
    private lateinit var binding: CommunityGroupPostBinding
    private val commentViewModel:CommentViewModel by lazy{
        ViewModelProvider(this).get(CommentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunityGroupPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView:RecyclerView=binding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(this)
        val comments=commentViewModel.commentList
        var adapter=MyAdapter(comments)
        recyclerView.adapter=adapter

        //포스트 테스트
        val post_title=intent.getStringExtra("title")
        val post_content=intent.getStringExtra("content")

        val title: TextView =binding.postTitle
        val content: TextView =binding.postContent
        title.text=post_title
        content.text=post_content

    }
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var comment:Comment
        private val nickname: TextView =itemView.findViewById(R.id.nickname)
        private val text: TextView =itemView.findViewById(R.id.comment)
        private val image:ImageView=itemView.findViewById(R.id.profileImage)
        fun bind(comment:Comment){
            this.comment=comment
            nickname.text=this.comment.nickname
            text.text=this.comment.comment
            image.setImageResource(this.comment.profileID)
        }

    }
    inner class MyAdapter(var list:List<Comment>): RecyclerView.Adapter<MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val inflater=LayoutInflater.from(parent.context)
            val view=inflater.inflate(R.layout.item_comment, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val comment=list[position]
            holder.bind(comment)
        }
        override fun getItemCount()=list.size
    }

}