package com.example.todo.adapters.forRV

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.data.models.Item
import com.example.todo.data.models.AddNewGroup
import com.example.todo.data.models.group.Group
import com.example.todo.databinding.ItemAddNewGroupBinding
import com.example.todo.databinding.ItemGroupState2Binding
import com.example.todo.utils.TimeUtil

class GroupPickerRVAdapter(
    private val openBottomSheetToCreateNewGroup: () -> Unit,
    private val newGroupSelected: (String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var groupBinding: ItemGroupState2Binding
    private lateinit var addNewGroupBinding: ItemAddNewGroupBinding
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
            is AddNewGroup -> 0
            else -> 1
        }
    }

    //<(￣︶￣)>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                addNewGroupBinding = ItemAddNewGroupBinding.inflate(inflater, parent, false)
                AddNewGroupViewHolder(addNewGroupBinding.root)
            }
            else -> {
                groupBinding = ItemGroupState2Binding.inflate(inflater, parent, false)
                GroupWithTodosViewHolder(groupBinding.root)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        setContent(holder, position)
        setOnClick(holder, position)
    }

    private fun setContent(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupWithTodosViewHolder) {
            val currentGroupTitle = (items[position] as Group).title
            groupBinding.tvTitle.text =
                if (currentGroupTitle != "") currentGroupTitle else "Chưa phân loại"
        }
    }

    private fun setOnClick(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GroupWithTodosViewHolder -> {
                groupBinding.cardViewTodos.setOnClickListener {
                    newGroupSelected((items[position] as Group).title)
                }
            }
            is AddNewGroupViewHolder -> {
                addNewGroupBinding.cardViewBtnAddNewTodos.setOnClickListener {
                    Log.d("CardView:", "Clicked")
                    openBottomSheetToCreateNewGroup()
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}