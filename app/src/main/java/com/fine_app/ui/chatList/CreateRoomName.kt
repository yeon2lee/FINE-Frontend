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
import androidx.core.content.ContextCompat
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
        var imageNum=11
        binding.imageButton.setOnClickListener {
            binding.imageButton.background=ContextCompat.getDrawable(this, R.drawable.box_border2)
            binding.imageButton2.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton3.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton4.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton5.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            imageNum=15
            Log.d("image", "이미지 클릭: ${imageNum}")
        }
        binding.imageButton2.setOnClickListener {
            binding.imageButton.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton2.background=ContextCompat.getDrawable(this, R.drawable.box_border2)
            binding.imageButton3.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton4.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton5.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            imageNum=14
            Log.d("image", "이미지 클릭: ${imageNum}")

        }
        binding.imageButton3.setOnClickListener {
            binding.imageButton.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton2.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton3.background=ContextCompat.getDrawable(this, R.drawable.box_border2)
            binding.imageButton4.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton5.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            imageNum=13
            Log.d("image", "이미지 클릭: ${imageNum}")

        }
        binding.imageButton4.setOnClickListener {
            binding.imageButton.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton2.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton3.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton4.background=ContextCompat.getDrawable(this, R.drawable.box_border2)
            binding.imageButton5.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            imageNum=12
            Log.d("image", "이미지 클릭: ${imageNum}")

        }
        binding.imageButton5.setOnClickListener {
            binding.imageButton.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton2.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton3.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton4.background=ContextCompat.getDrawable(this, R.drawable.box_border)
            binding.imageButton5.background=ContextCompat.getDrawable(this, R.drawable.box_border2)
            imageNum=11
            Log.d("image", "이미지 클릭: ${imageNum}")

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
            Log.d("image", "이미지 최종: ${imageNum}")

            setResult(RESULT_OK, intent)
            finish()
        }
        binding.backButton2.setOnClickListener {
            finish()
        }
        setContentView(binding.root)
    }
}