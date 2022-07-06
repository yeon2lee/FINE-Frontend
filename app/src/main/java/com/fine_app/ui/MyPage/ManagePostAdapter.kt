package com.fine_app.ui.MyPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.R
import com.fine_app.databinding.ListItemMypagePostBinding
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
        if (myDataset[position].group_check) {
            binding.mypagePostCommunityTypeTv.text = "그룹 커뮤니티"
        } else {
            binding.mypagePostCommunityTypeTv.text = "일반 커뮤니티"
        }

        val string = myDataset[position].createdDate
        val date = LocalDate.parse(string, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("M/d")
        val formatted = date.format(formatter)
        binding.mypagePostDateTv.text = formatted.toString()

        binding.mypagePostCommentTv.text = myDataset[position].comments.size.toString()
    }

    override fun getItemCount(): Int = myDataset.size

}


