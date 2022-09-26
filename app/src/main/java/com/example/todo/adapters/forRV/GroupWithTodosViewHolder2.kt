package com.example.todo.adapters.forRV

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.todo.data.models.group.Group
import com.example.todo.databinding.ItemGroupState2Binding
import com.example.todo.databinding.ItemNoGroupBinding

class GroupWithTodosViewHolder2(private val binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun setContent(group: Group) {
        when (binding) {
            is ItemGroupState2Binding ->
                binding.tvTitle.text = group.title
            is ItemNoGroupBinding ->
                binding.tvTitle.text = "Chưa phân loại"
        }
    }

    fun setVisibility() {}
    fun setOnClick(group: Group, newGroupSelected: (String) -> Unit) {
        when (binding) {
            is ItemGroupState2Binding ->
                binding.cardViewTodos.setOnClickListener {
                    newGroupSelected(group.title)
                }
            is ItemNoGroupBinding ->
                binding.cardViewNoGroup.setOnClickListener {
                    newGroupSelected(group.title)
                }
        }

    }
}