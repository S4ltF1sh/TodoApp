package com.example.todo.adapters.forRV

import android.view.DragEvent
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.common.ItemsEditMode
import com.example.todo.data.models.GroupWithTodos
import com.example.todo.data.models.Item
import com.example.todo.databinding.ItemGroupBinding
import com.google.android.material.card.MaterialCardView

class GroupWithTodosViewHolder(private val binding: ItemGroupBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun setContent(groupWithTodos: GroupWithTodos) {
        binding.apply {
            tvTitle.text = groupWithTodos.group?.title
            tvNumOfTodo.text = groupWithTodos.todos.size.toString()
        }
    }

    fun setVisibility(getEditMode: () -> ItemsEditMode) {
        binding.cardViewGroup.isChecked = getEditMode() != ItemsEditMode.NONE
    }

    fun setOnClick(
        groupWithTodos: GroupWithTodos,
        getEditMode: () -> ItemsEditMode,
        itemAsGroupClicked: (String, FragmentNavigator.Extras) -> Unit,
        selectItem: (Item) -> Unit,
        unSelectItem: (Item) -> Unit,
        addTodoToGroup: (Int, String) -> Unit
    ) {
        binding.cardViewGroup.setOnClickListener {
            if (getEditMode() == ItemsEditMode.NONE) {
                val extras = FragmentNavigatorExtras(
                    binding.cardViewGroup to "groupDetail"
                )
                itemAsGroupClicked(groupWithTodos.group!!.title, extras)
            } else {
                (it as MaterialCardView).apply {
                    isChecked = !isChecked
                    if (isChecked)
                        selectItem(groupWithTodos)
                    else
                        unSelectItem(groupWithTodos)
                }
            }
        }

        binding.cardViewGroup.setOnLongClickListener {
            (it as MaterialCardView).apply {
                isChecked = !isChecked
                if (isChecked)
                    selectItem(groupWithTodos)
                else
                    unSelectItem(groupWithTodos)
            }
            true
        }

        binding.cardViewGroup.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_ENTERED -> {
                    (v as? MaterialCardView)?.strokeWidth = 8
                    v.invalidate()
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    (v as? MaterialCardView)?.strokeWidth = 0
                    v.invalidate()
                }
                DragEvent.ACTION_DROP -> {
                    val id = event.clipData.getItemAt(0).text.toString().toInt()
                    addTodoToGroup(id, groupWithTodos.group?.title ?: "")
                    (v as? MaterialCardView)?.strokeWidth = 0
                }
            }
            true
        }
    }
}