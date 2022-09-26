package com.example.todo.adapters.forRV

import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.common.ItemsEditMode
import com.example.todo.data.models.Item
import com.example.todo.data.models.todo.Todo
import com.example.todo.databinding.ItemTodoBinding
import com.example.todo.utils.TimeUtil
import com.example.todo.utils.TodoVisibility
import com.google.android.material.card.MaterialCardView

class TodoViewHolder(private val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
    fun setContent(todo: Todo) {
        binding.apply {
            tvTitle.text = todo.title
            tvNote.text = todo.note
            tvTime.text = TimeUtil.format(todo.alarmDate)
        }
    }

    fun setVisibility(todo: Todo) {
        TodoVisibility.setupVisibility(todo, binding)
    }

    fun setOnClick(
        todo: Todo,
        getEditMode: () -> ItemsEditMode,
        viewTodo: (Todo, FragmentNavigator.Extras) -> Unit,
        selectItem: (Item) -> Unit,
        unSelectItem: (Item) -> Unit,
        checkBoxClicked: (Todo, Int) -> Unit
    ) {
        binding.apply {
            cardViewTodo.setOnClickListener {
                if (getEditMode() == ItemsEditMode.NONE) {
                    val extras = FragmentNavigatorExtras(
                        binding.cardViewTodo to "todoDetail"
                    )
                    viewTodo(todo, extras)
                } else {
                    (it as MaterialCardView).apply {
                        isChecked = !isChecked
                        if (isChecked)
                            selectItem(todo)
                        else
                            unSelectItem(todo)
                    }
                }
            }
            cardViewTodo.setOnLongClickListener {
                (it as MaterialCardView).apply {
                    isChecked = !isChecked
                    if (isChecked)
                        selectItem(todo)
                    else
                        unSelectItem(todo)
                }

                true
            }
            checkboxTodoStatus.setOnClickListener { checkBoxClicked(todo, adapterPosition) }
        }
    }
}