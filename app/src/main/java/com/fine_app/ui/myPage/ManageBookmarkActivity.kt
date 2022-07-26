package com.fine_app.ui.myPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.fine_app.databinding.ActivityManageBookmarkBinding
import com.fine_app.ui.MyPage.ManagePostAdapter
import com.fine_app.ui.MyPage.Post

class ManageBookmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageBookmarkBinding
    //lateinit var userData: List<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBookmarkBinding.inflate(layoutInflater)

        setContentView(binding.root)

    }

}