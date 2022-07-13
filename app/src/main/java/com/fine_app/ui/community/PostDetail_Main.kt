package com.fine_app.ui.community

import android.content.Intent
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
import com.fine_app.Comment
import com.fine_app.R
import com.fine_app.databinding.CommunityMainPostBinding
import com.fine_app.ui.MyPage.ManagePostActivity

//import com.fine_app.retrofit.RetrofitManager

class PostDetail_Main : AppCompatActivity() {
    private lateinit var binding: CommunityMainPostBinding
    /*
    private val commentViewModel:CommentViewModel by lazy{
        ViewModelProvider(this).get(CommentViewModel::class.java)
    }

     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunityMainPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val backButton=binding.backButton
        backButton.setOnClickListener{
            finish()
        }

        binding.writerImage.setOnClickListener{
            val userProfile = Intent(this, ShowUserProfileActivity::class.java)
            startActivity(userProfile)
        }

        //포스트 테스트
        val postTitle=intent.getStringExtra("title")
        val postContent=intent.getStringExtra("content")
        val postWriter=intent.getStringExtra("nickname")
        //val postWriterImage=intent.getIntExtra("profileID", 0)

        val title: TextView =binding.postTitle
        val content: TextView =binding.postContent
        val nickname:TextView=binding.writerName
        //val image:ImageView=binding.writerImage

        title.text=postTitle
        content.text=postContent
        nickname.text=postWriter
        //image.setImageResource(postWriterImage)

        //-----------------------------댓글------------------------------------------------------
        val recyclerView:RecyclerView=binding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(this)
        //val comments=commentViewModel.commentList
        val comments= intent.getSerializableExtra("comments") as ArrayList<Comment>
        val adapter=MyAdapter(comments)
        recyclerView.adapter=adapter

        // 사용자 프로필 조회
        binding.writerImage.setOnClickListener{
            val userProfile = Intent(this, ShowUserProfileActivity::class.java)
            startActivity(userProfile)
        }
    }
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var comment: Comment
        private val nickname: TextView =itemView.findViewById(R.id.nickname)
        private val text: TextView =itemView.findViewById(R.id.comment)
        private val image:ImageView=itemView.findViewById(R.id.profileImage)
        fun bind(comment:Comment){
            this.comment=comment
            nickname.text=this.comment.nickname
            text.text=this.comment.text
            //image.setImageResource(this.comment.profileID)
        }

    }

    inner class MyAdapter(var list:ArrayList<Comment>): RecyclerView.Adapter<MyViewHolder>(){
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