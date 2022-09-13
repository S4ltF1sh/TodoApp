package com.example.todo.utils

import android.graphics.Paint
import android.view.View
import com.example.todo.R
import com.example.todo.common.TodoStatus
import com.example.todo.data.models.todo.Todo
import com.example.todo.databinding.ItemTodoBinding

object TodoVisibility {
    fun setupVisibility(
        currentTodo: Todo,
        itemTodoBinding: ItemTodoBinding
    ) {
        if (currentTodo.title.isEmpty())
            itemTodoBinding.tvTitle.visibility = View.GONE
        else
            itemTodoBinding.tvTitle.visibility = View.VISIBLE


        if (currentTodo.note.isEmpty())
            itemTodoBinding.tvNote.visibility = View.GONE
        else
            itemTodoBinding.tvNote.visibility = View.VISIBLE

        if (currentTodo.todoStatus != TodoStatus.ON_GOING) {
            itemTodoBinding.apply {
                tvTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvTitle.isEnabled = false
                tvNote.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvNote.isEnabled = false
                tvTime.isEnabled = false
                checkboxTodoStatus.setButtonDrawable(R.drawable.ic_restore)
            }
        }
    }
}