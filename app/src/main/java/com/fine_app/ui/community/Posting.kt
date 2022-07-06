package com.fine_app.ui.community

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fine_app.databinding.CommunityPostingBinding


class Posting : AppCompatActivity() {
    private lateinit var binding: CommunityPostingBinding
    ////private lateinit var postModel:PostModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunityPostingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val title=binding.inputTitle.text.toString()
        val content:String =binding.inputContents.text.toString()
        val spinner: Spinner = binding.spinner
        val items = arrayOf("인원 선택", 1, 2, 3, 4, 5, 6)
        var capacity="0"
        ////postModel= PostModel()


        spinner.adapter= ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> { capacity="1"
                    }
                    1 -> capacity="1"
                    2 -> capacity="2"
                    3 -> capacity="3"
                    4 -> capacity="4"
                    5 -> capacity="5"
                    6 -> capacity="6"
                    else -> {}
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}

        }
        val backButton=binding.backButton
        backButton.setOnClickListener{
            finish()
        }
        val finButton=binding.finButton
        /*
        fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager1) {
            var ft: FragmentTransaction = fragmentManager.beginTransaction()
            ft.detach(fragment).attach(fragment).commit()
        }

         */
        finButton.setOnClickListener{
            finish()
/*
            val postviewmodel=PostViewModel()

            postviewmodel.post =
                com.fine_app.Post(
                    "글1",
                    "사용자1",
                    title,
                    content,
                    "10",
                    "2022-06-24T15:09:24.964117",
                    "2022-06-24T15:09:25.17818",
                    false,
                    false,
                    capacity.toInt(),
                    arrayListOf(Comment("댓글1", "안녕하세요", R.drawable.ic_launcher_foreground))
                )
                Post("닉네임", R.drawable.ic_launcher_foreground,title, content, "0", "0", capacity)
            Log.d("test", "글쓰기 종료")
            var ft: FragmentTransaction = supportFragmentManager!!.beginTransaction()
            ft.detach(CommunityMainFragment()).attach(CommunityMainFragment()).commit()
            finish()
            Log.d("test", "정보넘겨줌")


            /*
            var main=CommunityMainFragment()
            var bundle = Bundle()
            bundle.putString("nickname","닉네임")
            bundle.putInt("image",R.drawable.ic_launcher_foreground)
            bundle.putString("title",title)
            bundle.putString("content",content)
            bundle.putString("capacity",capacity)
            main.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
            supportFragmentManager!!.beginTransaction()
                .commit()

             */

 */
        }
    }
}