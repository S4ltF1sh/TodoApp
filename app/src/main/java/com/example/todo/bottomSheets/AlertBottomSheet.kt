package com.example.todo.bottomSheets

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.todo.R
import com.example.todo.databinding.BottomSheetAlertBinding
import com.example.todo.utils.SoftKeyBoardUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AlertBottomSheet(private val text: String, private val buttonClicked: (Boolean) -> Unit) :
    BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetAlertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FABBottomSheet)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setContent()
        setOnClick()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setContent() {
        binding.tvDialogTitle.text = text
        SoftKeyBoardUtil.showSoftKeyboard(requireActivity())
    }

    private fun setOnClick() {
        binding.apply {
            btnCancel.setOnClickListener { buttonCancelClicked() }
            btnOK.setOnClickListener { buttonOKClicked() }
        }
    }

    private fun buttonCancelClicked() {
        buttonClicked(false)
        this.dismiss()
    }


    private fun buttonOKClicked() {
        buttonClicked(true)
        this.dismiss()
    }
}