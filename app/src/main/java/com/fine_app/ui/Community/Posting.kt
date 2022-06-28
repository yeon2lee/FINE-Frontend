package com.fine_app.ui.Community

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.databinding.CommunityMainPostBinding
import com.fine_app.databinding.CommunityPostingBinding


class Posting : AppCompatActivity() {
    private lateinit var binding: CommunityPostingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunityPostingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val spinner: Spinner = binding.spinner
        val items = arrayOf("인원 선택", 1, 2, 3, 4, 5, 6)
        spinner.adapter= ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> { //기본값"인원 선택"

                    }
                    1 -> {

                    }
                    2 -> {

                    }
                    3 -> {

                    }
                    4 -> {

                    }
                    5 -> {

                    }
                    6 -> {

                    }
                    else -> {

                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }

        }

    }
}