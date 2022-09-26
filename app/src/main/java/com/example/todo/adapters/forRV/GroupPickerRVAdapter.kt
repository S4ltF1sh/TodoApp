package com.example.todo.adapters.forRV

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.data.models.Item
import com.example.todo.data.models.AddNewGroup
import com.example.todo.data.models.group.Group
import com.example.todo.databinding.ItemAddNewGroupBinding
import com.example.todo.databinding.ItemGroupState2Binding
import com.example.todo.databinding.ItemNoGroupBinding
import com.example.todo.utils.TimeUtil

class GroupPickerRVAdapter(
    private val openBottomSheetToCreateNewGroup: () -> Unit,
    private val newGroupSelected: (String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items: MutableList<Item> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Item>) {
        this.items.add(AddNewGroup())
        this.items.add(Group("", TimeUtil.currentTime()))
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            //create new group:
            is AddNewGroup -> 0
            else -> {
                //no group:
                if (position == 1)
                    1
                //default group:
                else
                    2
            }
        }
    }

    //<(￣︶￣)>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                val addNewGroupBinding = ItemAddNewGroupBinding.inflate(inflater, parent, false)
                AddNewGroupViewHolder(addNewGroupBinding)
            }
            1 -> {
                val noGroupBinding = ItemNoGroupBinding.inflate(inflater, parent, false)
                GroupWithTodosViewHolder2(noGroupBinding)
            }
            else -> {
                val groupBinding = ItemGroupState2Binding.inflate(inflater, parent, false)
                GroupWithTodosViewHolder2(groupBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBind(holder, position)
    }

    private fun onBind(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GroupWithTodosViewHolder2 -> {
                val currentGroup = items[position] as Group
                holder.setContent(currentGroup)
                holder.setOnClick(currentGroup, newGroupSelected)
            }
            is AddNewGroupViewHolder -> {
                holder.setOnClick(openBottomSheetToCreateNewGroup)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}