package com.fine_app.ui.myPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fine_app.databinding.ActivityAuthLocationBinding
import com.fine_app.databinding.ActivityAuthUniversityBinding

class AuthUniversityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthUniversityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthUniversityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}