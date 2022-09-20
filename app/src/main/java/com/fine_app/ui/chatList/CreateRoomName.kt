package com.fine_app.ui.chatList

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.view.WindowManager
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        var imageNum=1
        binding.radioGroup.setOnCheckedChangeListener{_, checkedId ->
            when(checkedId){
                R.id.radioButton1 -> {
                    imageNum=1
                    Log.d("dd", "1번 체크")
                }
                R.id.radioButton2 -> {
                    imageNum=2
                    Log.d("dd", "2번 체크")
                }
                R.id.radioButton3 -> {
                    imageNum=3
                    Log.d("dd", "3번 체크")
                }
                R.id.radioButton4 -> {
                    imageNum=4
                    Log.d("dd", "3번 체크")
                }
                R.id.radioButton5 -> {
                    imageNum=5
                    Log.d("dd", "3번 체크")
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
        binding.backButton2.setOnClickListener {
            finish()
        }
        setContentView(binding.root)
    }
}