package com.example.todo.adapters.forRV

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.common.ItemsEditMode
import com.example.todo.data.models.todo.Todo
import com.example.todo.databinding.ItemTodoBinding
import com.example.todo.common.TodoStatus.*
import com.example.todo.data.models.Item
import com.example.todo.utils.TimeUtil
import com.example.todo.utils.TodoVisibility
import com.google.android.material.card.MaterialCardView

class TodoRVAdapter(
    private val todos: List<Todo>,
    private val viewTodo: (Todo) -> Unit,
    private val checkBoxClicked: (Todo) -> Unit,
    private val getEditMode: () -> ItemsEditMode,
    private val selectItem: (Item) -> Unit,
    private val unSelectItem: (Item) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private lateinit var binding: ItemTodoBinding

    class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int = todos.size

    override fun getItemViewType(position: Int): Int {
        return when (todos[position].todoStatus) {
            ON_GOING -> 0
            DONE -> 1
            DELETED -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        binding = ItemTodoBinding.inflate(inflater, parent, false)
        return TodoViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        setVisibility(position)
        setContent(position)
        setOnClick(position)
    }

    private fun setVisibility(position: Int) {
        val currentTodo = todos[position]
        TodoVisibility.setupVisibility(currentTodo, binding)
    }

    private fun setContent(position: Int) {
        val currentTodo = todos[position]
        binding.apply {
            tvTitle.text = currentTodo.title
            tvNote.text = currentTodo.note
            tvTime.text = TimeUtil.format(currentTodo.alarmDate)
        }
    }

    private fun setOnClick(position: Int) {
        val currentTodo = todos[position]
        binding.apply {
            cardViewTodo.setOnClickListener {
                if (getEditMode() == ItemsEditMode.NONE)
                    viewTodo(currentTodo)
                else {
                    (it as MaterialCardView).apply {
                        isChecked = !isChecked
                        if (isChecked)
                            selectItem(currentTodo)
                        else
                            unSelectItem(currentTodo)
                    }
                }
            }
            cardViewTodo.setOnLongClickListener {
                (it as MaterialCardView).apply {
                    isChecked = !isChecked
                    if (isChecked)
                        selectItem(currentTodo)
                    else
                        unSelectItem(currentTodo)
                }

                true
            }
            checkboxTodoStatus.setOnClickListener { checkBoxClicked(currentTodo) }
        }
    }
}
