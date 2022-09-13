package com.example.todo.fragments.share

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.todo.common.Const.TODO_NEED_TO_SHARE
import com.example.todo.databinding.FragmentShareTodoInImageFormatBinding

class ShareTodoInImageFormatFragment : Fragment() {
    private lateinit var binding: FragmentShareTodoInImageFormatBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShareTodoInImageFormatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setContent()
        setOnClick()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setContent() {
        val byteArray = arguments?.getByteArray(TODO_NEED_TO_SHARE)
        val bitmap = byteArray?.let { BitmapFactory.decodeByteArray(byteArray, 0, it.size) }
        binding.imvTodo.setImageBitmap(bitmap)
    }

    private fun setOnClick() {
        binding.apply {
            toolBar.setNavigationOnClickListener { findNavController().popBackStack() }
            btnSaveToDevice.setOnClickListener { }
            btnShareToOtherApp.setOnClickListener { }
        }
    }
}