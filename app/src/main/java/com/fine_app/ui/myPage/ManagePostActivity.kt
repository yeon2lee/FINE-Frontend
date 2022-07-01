package com.fine_app.ui.myPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.fine_app.databinding.ActivityMypagePostBinding

class ManagePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypagePostBinding
    private val data = arrayListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMypagePostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        for (i in 1..10) {
            data.add(Post("아침마다 기상 인증해요"))
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ManagePostAdapter(data)
    }
}