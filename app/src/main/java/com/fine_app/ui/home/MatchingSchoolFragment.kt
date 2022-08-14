package com.fine_app.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.R
import com.fine_app.Test
import com.fine_app.databinding.FragmentMatchingBinding



class MatchingSchoolFragment : Fragment() {

    private var _binding: FragmentMatchingBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMatchingBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //테스트용 데이터
        val post1= Test("한이음", "안녕하세요\n만나서 반갑습니다아아아ㅏ")
        val post2= Test("한이음2", "안녕하세요\n저는 코딩이 취미예요..^^")
        val post3= Test("한이음3", "안녕하세요\n만나서 반갑습니다아아아ㅏ")
        val post4= Test("한이음4", "안녕하세요\n저는 코딩이 취미예요..^^")
        val testList=ArrayList<Test>()
        testList.add(post1)
        testList.add(post2)
        testList.add(post3)
        testList.add(post4)

        val items = arrayOf("신규순", "오래된순")
        binding.spinner3.adapter= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        binding.spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                when (position) {
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
        fun bind(test: Test) {
            this.test=test
            nickname.text=this.test.name
            intro.text=this.test.content

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