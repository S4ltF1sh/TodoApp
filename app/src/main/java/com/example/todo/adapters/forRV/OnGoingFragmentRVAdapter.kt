package com.example.todo.adapters.forRV

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Rect
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.common.ItemsEditMode
import com.example.todo.data.models.Item
import com.example.todo.data.models.GroupWithTodos
import com.example.todo.data.models.todo.Todo
import com.example.todo.databinding.ItemGroupBinding
import com.example.todo.databinding.ItemTodoBinding
import com.example.todo.utils.TimeUtil
import com.example.todo.utils.TodoVisibility
import com.google.android.material.card.MaterialCardView

class OnGoingFragmentRVAdapter(
    private val items: List<Item>,
    private val itemAsTodoClicked: (Todo) -> Unit,
    private val itemAsGroupClicked: (GroupWithTodos) -> Unit,
    private val getEditMode: () -> ItemsEditMode,
    private val checkDoneTodo: (Todo) -> Unit,
    private val addTodoToGroup: (Int, String) -> Unit,
    private val selectItem: (Item) -> Unit,
    private val unSelectItem: (Item) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var todoBinding: ItemTodoBinding
    private lateinit var groupBinding: ItemGroupBinding
    private var isLongPressed = false

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
                todoBinding = ItemTodoBinding.inflate(inflater, parent, false)
                TodoViewHolder(todoBinding.root)
            }
            else -> {
                groupBinding = ItemGroupBinding.inflate(inflater, parent, false)
                GroupWithTodosViewHolder(groupBinding.root)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        setContent(holder, position)
        setVisibility(holder, position)
        setOnClick(holder, position)
    }

    //set:
    private fun setContent(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TodoViewHolder -> {
                val currentTodo = items[position] as Todo
                todoBinding.apply {
                    tvTitle.text = currentTodo.title
                    tvNote.text = currentTodo.note
                    tvTime.text = TimeUtil.format(currentTodo.alarmDate)
                }
            }

            is GroupWithTodosViewHolder -> {
                val currentGroupWithTodos = items[position] as GroupWithTodos
                groupBinding.apply {
                    tvTitle.text = currentGroupWithTodos.group?.title
                    tvNumOfTodo.text = currentGroupWithTodos.todos.size.toString()
                }
            }
        }
    }

    private fun setVisibility(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TodoViewHolder -> {
                val currentTodo = items[position] as Todo
                TodoVisibility.setupVisibility(currentTodo, todoBinding)
                todoBinding.cardViewTodo.isChecked = getEditMode() != ItemsEditMode.NONE
            }

            is GroupWithTodosViewHolder -> {
                groupBinding.cardViewGroup.isChecked = getEditMode() != ItemsEditMode.NONE
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClick(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TodoViewHolder -> {
                val currentTodo = items[position] as Todo
                todoBinding.cardViewTodo.setOnClickListener {
                    if (getEditMode() == ItemsEditMode.NONE)
                        itemAsTodoClicked(currentTodo)
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

                todoBinding.cardViewTodo.setOnLongClickListener {
                    isLongPressed = true
                    true
                }

                todoBinding.cardViewTodo.setOnTouchListener { view, event ->
                    val x = event.rawX.toInt()
                    val y = event.rawY.toInt()

                    if (isLongPressed) {
                        if (event.action == MotionEvent.ACTION_MOVE && getEditMode() == ItemsEditMode.NONE) {
                            val item = ClipData.Item(currentTodo.id.toString())
                            val dragData = ClipData(
                                "todo_id",
                                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                                item
                            )

                            val myShadow = View.DragShadowBuilder(view)

                            // Start the drag.
                            view.startDragAndDrop(
                                dragData,  // The data to be dragged
                                myShadow,  // The drag shadow builder
                                view,      // No need to use local data
                                0          // Flags (not currently used, set to 0)
                            )

                            isLongPressed = false
                        } else if (event.action == MotionEvent.ACTION_UP) {
                            //Check if view child contain cursor:
                            val outRect = Rect()
                            val location = IntArray(2)
                            view.getDrawingRect(outRect)
                            view.getLocationOnScreen(location)
                            outRect.offset(location[0], location[1])

                            if (outRect.contains(x, y))
                                (view as? MaterialCardView)?.apply {
                                    isChecked = !isChecked
                                    if (isChecked)
                                        selectItem(currentTodo)
                                    else
                                        unSelectItem(currentTodo)
                                }

                            isLongPressed = false
                        }
                    }

                    false
                }

                todoBinding.checkboxTodoStatus.setOnClickListener {
                    checkDoneTodo(currentTodo)
                }
            }

            is GroupWithTodosViewHolder -> {
                val currentGroup = items[position] as GroupWithTodos
                groupBinding.cardViewGroup.setOnClickListener {
                    if (getEditMode() == ItemsEditMode.NONE)
                        itemAsGroupClicked(currentGroup)
                    else {
                        (it as MaterialCardView).apply {
                            isChecked = !isChecked
                            if (isChecked)
                                selectItem(currentGroup)
                            else
                                unSelectItem(currentGroup)
                        }
                    }
                }

                groupBinding.cardViewGroup.setOnLongClickListener {
                    (it as MaterialCardView).apply {
                        isChecked = !isChecked
                        if (isChecked)
                            selectItem(currentGroup)
                        else
                            unSelectItem(currentGroup)
                    }
                    true
                }

                groupBinding.cardViewGroup.setOnDragListener { v, event ->
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
                            addTodoToGroup(id, currentGroup.group?.title ?: "")
                        }
                    }
                    true
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}