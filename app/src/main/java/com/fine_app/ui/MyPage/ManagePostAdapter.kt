package com.fine_app.ui.MyPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.R
import com.fine_app.databinding.ListItemMypagePostBinding


class ManagePostAdapter(private val myDataset: List<Post>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //view 대신 binding으로 넣어준다. view-> recyclerview.viewholder안에도 binding.root로 변환.
    class MyViewHolder(var binding: ListItemMypagePostBinding) : RecyclerView.ViewHolder(binding.root)
//binding의 타입은 item ListItemMypagePostBinding 이유는 리사이클러뷰에 각각 하나씩 들어가는 itemlayout의 이름이 list_item_mypage_post이기때문이다.

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_mypage_post, viewGroup, false)
        return MyViewHolder(ListItemMypagePostBinding.bind(view)) //binding을 access하기위해 ListItemMypagePostBinding.bind(view)로
    }

    //findviewbyId대신 viewholder:binding으로 view access
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        binding.itemData.text = myDataset[position].title
    }

    override fun getItemCount(): Int = myDataset.size

}


