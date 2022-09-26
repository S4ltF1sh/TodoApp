package com.example.todo.widgets

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.example.todo.R
import com.example.todo.common.Const
import com.example.todo.common.TodoStatus
import com.example.todo.data.MyDatabase
import com.example.todo.data.models.todo.Todo


class TodoRemoteViewsFactory(private val context: Context, private val intent: Intent) :
    RemoteViewsFactory {
    val todos: MutableList<Todo> = mutableListOf()

    override fun onCreate() {
        initData()
    }

    override fun onDataSetChanged() {
        initData()
    }

    override fun onDestroy() {
        //TODO("Not yet implemented")
    }

    override fun getCount(): Int = todos.size

    override fun getViewAt(position: Int): RemoteViews {
        val currentTodo = todos[position]
        val remoteViews = RemoteViews(
            context.packageName,
            R.layout.item_todo_widget
        ).apply {
            setTextViewText(R.id.tvTitle, currentTodo.title)

            if (currentTodo.alarmDate == null)
                setViewVisibility(R.id.imvHaveAlarm, View.GONE)

            //combine
            val fillInIntent = Intent().apply {
                putExtra(Const.ID_TODO_NEED_TO_VIEW, currentTodo.id)
            }

            setOnClickFillInIntent(R.id.todoWidgetItem, fillInIntent)
        }


        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = todos[position].id.toLong()

    override fun hasStableIds(): Boolean = true

    private fun initData() {
        this.todos.clear()
        val todos = MyDatabase.getInstance(context).todoDao.getByTodoStatus(TodoStatus.ON_GOING)
        this.todos.addAll(todos)
    }
}