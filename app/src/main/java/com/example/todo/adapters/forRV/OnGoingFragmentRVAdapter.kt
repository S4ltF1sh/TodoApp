package com.example.todo.adapters.forRV

import android.annotation.SuppressLint
import android.view.*
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.common.ItemsEditMode
import com.example.todo.data.models.Item
import com.example.todo.data.models.GroupWithTodos
import com.example.todo.data.models.todo.Todo
import com.example.todo.databinding.ItemGroupBinding
import com.example.todo.databinding.ItemTodoBinding

class OnGoingFragmentRVAdapter(
    private val itemAsTodoClicked: (Todo, FragmentNavigator.Extras) -> Unit,
    private val itemAsGroupClicked: (String, FragmentNavigator.Extras) -> Unit,
    private val getEditMode: () -> ItemsEditMode,
    private val checkDoneTodo: (Todo) -> Unit,
    private val addTodoToGroup: (Int, String) -> Unit,
    private val selectItem: (Item) -> Unit,
    private val unSelectItem: (Item) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isLongPressed = false
    private val items = mutableListOf<Item>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: List<Item>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    //override:
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Todo -> 0
            is GroupWithTodos -> 1
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            0 -> {
                val todoBinding = ItemTodoBinding.inflate(inflater, parent, false)
                TodoAtOnGoingFragmentViewHolder(todoBinding)
            }
            else -> {
                val groupBinding = ItemGroupBinding.inflate(inflater, parent, false)
                GroupWithTodosViewHolder(groupBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBind(holder, position)
    }

    //set:
    private fun onBind(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TodoAtOnGoingFragmentViewHolder -> {
                val currentTodo = items[position] as Todo
                val getLongPressedState = { isLongPressed }
                val setLongPressedState = { newState: Boolean -> isLongPressed = newState }
                holder.setContent(currentTodo)
                holder.setVisibility(currentTodo)
                holder.setOnClick(
                    currentTodo,
                    itemAsTodoClicked,
                    getEditMode,
                    checkDoneTodo,
                    selectItem,
                    unSelectItem,
                    getLongPressedState,
                    setLongPressedState
                )
            }

            is GroupWithTodosViewHolder -> {
                val currentGroupWithTodos = items[position] as GroupWithTodos
                holder.setContent(currentGroupWithTodos)
                holder.setVisibility(getEditMode)
                holder.setOnClick(
                    currentGroupWithTodos,
                    getEditMode,
                    itemAsGroupClicked,
                    selectItem,
                    unSelectItem,
                    addTodoToGroup
                )

            }
        }
    }

    override fun getItemCount(): Int = items.size
}