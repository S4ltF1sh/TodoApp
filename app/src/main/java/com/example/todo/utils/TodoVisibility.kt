package com.example.todo.utils

import android.annotation.SuppressLint
import android.graphics.Color
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
        itemTodoBinding.cardViewTodo.isChecked = false
        itemTodoBinding.checkboxTodoStatus.isChecked = false
        if (currentTodo.title.isEmpty())
            itemTodoBinding.tvTitle.visibility = View.GONE
        else
            itemTodoBinding.tvTitle.visibility = View.VISIBLE


        if (currentTodo.note.isEmpty())
            itemTodoBinding.tvNote.visibility = View.GONE
        else
            itemTodoBinding.tvNote.visibility = View.VISIBLE

        @SuppressLint("ResourceAsColor")
        if (currentTodo.todoStatus != TodoStatus.ON_GOING) {
            itemTodoBinding.apply {
                tvTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvTitle.isEnabled = false
                tvNote.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvNote.isEnabled = false
                tvTime.isEnabled = false
                tvTime.setTextColor(Color.parseColor("#616161"))
                checkboxTodoStatus.setButtonDrawable(R.drawable.ic_restore)
            }
        } else {
            if (currentTodo.alarmDate != null && currentTodo.alarmDate!! < TimeUtil.currentTime())
                itemTodoBinding.tvTime.setTextColor(Color.parseColor("#ef476f"))
        }
    }
}