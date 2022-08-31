package com.fine_app.ui.community

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.fine_app.databinding.DialogLayoutBinding

class ConfirmDialog(
    confirmDialogInterface: ConfirmDialogInterface,
    text: String, num: Int, theme:Int
) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogLayoutBinding? = null
    private val binding get() = _binding!!

    private var confirmDialogInterface: ConfirmDialogInterface? = null

    private var text: String? = null
    private var num: Int? = null
    private var theme: Int? = null

    init {
        this.text = text
        this.num = num
        this.theme=theme
        this.confirmDialogInterface = confirmDialogInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLayoutBinding.inflate(inflater, container, false)
        val view = binding.root

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.confirmTextView.text = text
        // 취소 버튼 클릭
        binding.noButton.setOnClickListener {
            dismiss()
        }
        // 확인 버튼 클릭
        binding.okButton.setOnClickListener {
            this.confirmDialogInterface?.onYesButtonClick(num!!,theme!!)
            dismiss()
        }
        if(this.theme==0){ //버튼 두 개
            binding.noButton.visibility=View.VISIBLE
            binding.okButton.visibility=View.VISIBLE
        }else{ //텍스트만
            binding.noButton.visibility=View.GONE
            binding.okButton.visibility=View.GONE
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface ConfirmDialogInterface {
    fun onYesButtonClick(num: Int, theme: Int)
}