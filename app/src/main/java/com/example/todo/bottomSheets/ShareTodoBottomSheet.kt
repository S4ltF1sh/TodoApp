package com.example.todo.bottomSheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todo.databinding.BottomSheetShareTodoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ShareTodoBottomSheet(private val shareInImageFormat: () -> Unit) :
    BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetShareTodoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetShareTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setOnclick()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setOnclick() {
        binding.apply {
            tvShareInTextFormat.setOnClickListener { }
            tvShareInImageFormat.setOnClickListener {
                shareInImageFormat()
                dismiss()
            }
            btnCancel.setOnClickListener { dismiss() }
        }
    }
}