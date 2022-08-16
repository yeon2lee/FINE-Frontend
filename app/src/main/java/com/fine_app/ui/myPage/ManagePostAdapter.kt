package com.fine_app.ui.myPage

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.R
import com.fine_app.databinding.ListItemMypagePostBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.community.PostDetail_Main
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ManagePostAdapter(private val myDataset: List<Post>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class MyViewHolder(var binding: ListItemMypagePostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_mypage_post, viewGroup, false)
        return MyViewHolder(ListItemMypagePostBinding.bind(view)) //binding을 access하기위해 ListItemMypagePostBinding.bind(view)로
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        binding.mypagePostTitleTv.text = myDataset[position].title
        binding.mypagePostContentTv.text = myDataset[position].content
        if (myDataset[position].group_check) {
            binding.mypagePostCommunityTypeTv.text = "그룹 커뮤니티"
        } else {
            binding.mypagePostCommunityTypeTv.text = "일반 커뮤니티"
        }

        binding.itemRoot.setOnClickListener{
            itemClickListener.onClick(it, position)
        }

        val string = myDataset[position].createdDate
        val date = LocalDate.parse(string, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("M/d")
        val formatted = date.format(formatter)
        binding.mypagePostDateTv.text = formatted.toString()

        binding.mypagePostCommentTv.text = myDataset[position].comments.size.toString()
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

    override fun getItemCount(): Int = myDataset.size

}


