package com.fine_app.ui.chatList

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.fine_app.CreateChatRoom
import com.fine_app.R
import com.fine_app.databinding.ChattingSettingBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateRoomName: AppCompatActivity(){

    private lateinit var binding: ChattingSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChattingSettingBinding.inflate(layoutInflater)
        var imageNum=1
        val buttonList: RadioGroup =binding.radioGroup
        buttonList.setOnCheckedChangeListener{_, checkedId ->
            when(checkedId){
                R.id.radioButton -> {
                    imageNum=1
                }
                R.id.radioButton2 -> {
                    imageNum=2
                }
                R.id.radioButton3 -> {
                    imageNum=3
                }
                R.id.radioButton4 -> {
                    imageNum=4
                }
                R.id.radioButton5 -> {
                    imageNum=5
                }
                R.id.radioButton6 -> {
                    imageNum=6
                }
            }
        }

        var text=""
        binding.putRoomName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                text=p0.toString()
            }
        })
        binding.button.setOnClickListener {
            val intent = Intent()
            intent.putExtra("text", text)
            intent.putExtra("image", imageNum)
            setResult(RESULT_OK, intent)
            finish()
        }
        setContentView(binding.root)
    }
}