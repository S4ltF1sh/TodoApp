package com.example.todo.adapters.forRV

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.common.ItemsEditMode
import com.example.todo.data.models.todo.Todo
import com.example.todo.databinding.ItemTodoBinding
import com.example.todo.common.TodoStatus.*
import com.example.todo.data.models.Item

class TodoRVAdapter(
    private val viewTodo: (Todo, FragmentNavigator.Extras) -> Unit,
    private val checkBoxClicked: (Todo, Int) -> Unit,
    private val getEditMode: () -> ItemsEditMode,
    private val selectItem: (Item) -> Unit,
    private val unSelectItem: (Item) -> Unit,
) :
    RecyclerView.Adapter<TodoViewHolder>() {

    private val todos = mutableListOf<Todo>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(todos: List<Todo>) {
        this.todos.clear()
        this.todos.addAll(todos)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        todos.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = todos.size

    override fun getItemViewType(position: Int): Int {
        return when (todos[position].todoStatus) {
            ON_GOING -> 0
            DONE -> 1
            DELETED -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTodoBinding.inflate(inflater, parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentTodo = todos[position]
        holder.setContent(currentTodo)
        holder.setVisibility(currentTodo)
        holder.setOnClick(
            currentTodo,
            getEditMode,
            viewTodo,
            selectItem,
            unSelectItem,
            checkBoxClicked
        )
    }
}
