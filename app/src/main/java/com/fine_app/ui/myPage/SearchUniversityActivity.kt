package com.fine_app.ui.myPage

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.R
import com.fine_app.databinding.ActivitySearchUniversityBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchUniversityActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchUniversityBinding
    private lateinit var recyclerView: RecyclerView
    lateinit var search: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchUniversityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        search = Intent()

        binding.searchUniversityEt.setQuery(search.getStringExtra("query"), false)

        binding.searchUniversityEt.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            //검색 버튼 눌렀을 때 호출
            override fun onQueryTextSubmit(p0: String): Boolean {
                searchUniversity(p0)
                return true
            }
            //텍스트 입력과 동시에 호출
            override fun onQueryTextChange(p0: String): Boolean {
                searchUniversity(p0)
                return true
            }
        })
        binding.cancelSearchBtn.setOnClickListener{
            finish()
        }
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var university: University
        private val title: TextView = itemView.findViewById(R.id.searchUniversity)!!

        fun bind(university: University) {
            this.university = university
            var schoolName = this.university.schoolName
            var link = this.university.link
            title.text = schoolName

            itemView.setOnClickListener{
                search.putExtra("schoolName", schoolName)
                search.putExtra("link", link.replace("http://", "").replace("www.", ""))
                setResult(Activity.RESULT_OK, search)
                finish()
            }
        }
    }
    inner class MyAdapter(val list:List<University>): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_search_university, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }

    private fun searchUniversity(title:String){
        val call: Call<DataSearch> = ServiceCreator.service.searchUniversity("efb0890dbac3202c498b279ad01a8e28", "api", "SCHOOL", "json", "univ_list", title)

        call.enqueue(object : Callback<DataSearch> {
            override fun onResponse(call: Call<DataSearch>, response: Response<DataSearch>) {
                val adapter = MyAdapter(response.body()?.dataSearch?.content!!)
                recyclerView = binding.universityList
                recyclerView.layoutManager= LinearLayoutManager(this@SearchUniversityActivity)
                recyclerView.adapter=adapter
            }
            override fun onFailure(call: Call<DataSearch>, t: Throwable) {
            }
        })
    }
}