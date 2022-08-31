package com.fine_app.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.R
import com.fine_app.Test
import com.fine_app.databinding.FragmentMatchingBinding

class MatchingAllFragment : Fragment() {
    private var _binding: FragmentMatchingBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMatchingBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //테스트용 데이터
        val post1= Test("한이음", "안녕하세요\n만나서 반갑습니다아!")
        val post2= Test("한이음2", "2호선 카페투어 하실 분~!")
        val post3= Test("한이음3", "서울 신림쪽 살고 있어용 카페 같이 다녀요!")
        val post4= Test("한이음4", "ISTJ \ns대 컴퓨터공학과 입니다!! ")
        val testList=ArrayList<Test>()
        testList.add(post1)
        testList.add(post2)
        testList.add(post3)
        testList.add(post4)

        val items = arrayOf("신규순", "신뢰도순")
        binding.spinner3.adapter= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        binding.spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                when (position) {
                    //todo api 연결
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        recyclerView=binding.recyclerView
        recyclerView.layoutManager= GridLayoutManager(context, 2)
        recyclerView.adapter=MyAdapter(testList)
        return root
    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){

        private lateinit var test: Test
        private val nickname: TextView =itemView.findViewById(R.id.matching_name)
        private val intro: TextView =itemView.findViewById(R.id.matching_intro)
        private val image: ImageView =itemView.findViewById(R.id.friend_image)
        fun bind(test: Test) {
            this.test=test
            nickname.text=this.test.name
            intro.text=this.test.content
            if(this.test.name=="한이음2") image.setImageResource(R.drawable.profile4)
            else if(this.test.name=="한이음3") image.setImageResource(R.drawable.profile2)
            else if(this.test.name=="한이음4") image.setImageResource(R.drawable.profile3)
            itemView.setOnClickListener{
            }
        }
    }
    inner class MyAdapter(val list:ArrayList<Test>): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_matching, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }
}