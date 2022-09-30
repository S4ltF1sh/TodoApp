package com.example.todo.adapters.forRV

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
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

class TodoAtOnGoingFragmentViewHolder(private val binding: ItemTodoBinding) :
    RecyclerView.ViewHolder(binding.root) {
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

    @SuppressLint("ClickableViewAccessibility")
    fun setOnClick(
        todo: Todo,
        itemAsTodoClicked: (Todo, FragmentNavigator.Extras) -> Unit,
        getEditMode: () -> ItemsEditMode,
        checkDoneTodo: (Todo) -> Unit,
        selectItem: (Item) -> Unit,
        unSelectItem: (Item) -> Unit,
        getLongPressedState: () -> Boolean,
        setLongPressedState: (Boolean) -> Unit
    ) {
        binding.cardViewTodo.setOnClickListener {
            if (getEditMode() == ItemsEditMode.NONE) {
                val extras = FragmentNavigatorExtras(
                    binding.cardViewTodo to "todoDetail"
                )
                itemAsTodoClicked(todo, extras)
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

        binding.cardViewTodo.setOnLongClickListener {
            setLongPressedState(true)
            true
        }

        binding.cardViewTodo.setOnTouchListener { view, event ->
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()

            if (getLongPressedState()) {
                if (event.action == MotionEvent.ACTION_MOVE && getEditMode() == ItemsEditMode.NONE) {
                    val item = ClipData.Item(todo.id.toString())
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

                    setLongPressedState(false)
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
                                selectItem(todo)
                            else
                                unSelectItem(todo)
                        }

                    setLongPressedState(false)
                }
            }

            false
        }

        binding.checkboxTodoStatus.setOnClickListener {
            checkDoneTodo(todo)
        }
    }
}