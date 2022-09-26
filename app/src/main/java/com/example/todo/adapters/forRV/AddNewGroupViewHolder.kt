package com.example.todo.adapters.forRV

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.ItemAddNewGroupBinding

class AddNewGroupViewHolder(private val binding: ItemAddNewGroupBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun setContent() {

    }

    fun setVisibility() {}
    fun setOnClick(openBottomSheetToCreateNewGroup: () -> Unit) {
        binding.cardViewBtnAddNewTodos.setOnClickListener {
            Log.d("CardView:", "Clicked")
            openBottomSheetToCreateNewGroup()
        }
    }
}