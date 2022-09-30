package com.example.todo.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.navigation.NavDeepLinkBuilder
import com.example.todo.R
import com.example.todo.common.Const
import com.example.todo.common.TodoStatus
import com.example.todo.common.ViewTodoStatus
import com.example.todo.data.MyDatabase
import com.example.todo.data.models.todo.Todo
import com.example.todo.utils.TimeUtil


class TodoWidget : AppWidgetProvider() {
    private val todos: MutableList<Todo?> = mutableListOf()
    override fun onReceive(context: Context?, intent: Intent?) {

        @Suppress("NAME_SHADOWING") val fourRecentTodos = context?.let { context ->
            MyDatabase.getInstance(context).todoDao.getByTodoStatus(TodoStatus.ON_GOING)
                .sortedBy { it.editDate }.reversed()
        }
        Log.i("TodoWidget.onReceive", "$fourRecentTodos")
        todos.clear()
        fourRecentTodos?.let { todos.addAll(it) }

        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.i("TodoWidget.onUpdate", "$todos")
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, todos)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        todos: List<Todo?>
    ) {
        val size = todos.size
        val remoteViews = RemoteViews(context.packageName, R.layout.todo_widget).apply {
            setOnClickPendingIntent(
                R.id.imvAdd,
                getPendingIntent(context, ViewTodoStatus.ADD_MODE, null)
            )
            if (size >= 1) {
                setViewVisibility(R.id.todo1, View.VISIBLE)
                val todo = todos[0]
                val title = if (todo?.title != "") todo?.title else todo.note
                setTextViewText(R.id.tvTitle1, title)
                if (todo!!.alarmDate != null && todo.alarmDate!! > TimeUtil.currentTime())
                    setViewVisibility(R.id.imvClock1, View.VISIBLE)
                else
                    setViewVisibility(R.id.imvClock1, View.GONE)
                setOnClickPendingIntent(
                    R.id.todo1,
                    getPendingIntent(context, ViewTodoStatus.VIEW_MODE, todo)
                )
            } else
                setViewVisibility(R.id.todo1, View.GONE)
            if (size >= 2) {
                setViewVisibility(R.id.todo2, View.VISIBLE)
                val todo = todos[1]
                val title = if (todo?.title != "") todo?.title else todo.note
                setTextViewText(R.id.tvTitle2, title)
                if (todo!!.alarmDate != null && todo.alarmDate!! > TimeUtil.currentTime())
                    setViewVisibility(R.id.imvClock2, View.VISIBLE)
                else
                    setViewVisibility(R.id.imvClock2, View.GONE)
                setOnClickPendingIntent(
                    R.id.todo2,
                    getPendingIntent(context, ViewTodoStatus.VIEW_MODE, todos[1])
                )
            } else setViewVisibility(R.id.todo2, View.GONE)
            if (size >= 3) {
                setViewVisibility(R.id.todo3, View.VISIBLE)
                val todo = todos[2]
                val title = if (todo?.title != "") todo?.title else todo.note
                setTextViewText(R.id.tvTitle3, title)
                if (todo!!.alarmDate != null && todo.alarmDate!! > TimeUtil.currentTime())
                    setViewVisibility(R.id.imvClock3, View.VISIBLE)
                else
                    setViewVisibility(R.id.imvClock3, View.GONE)
                setOnClickPendingIntent(
                    R.id.todo3,
                    getPendingIntent(context, ViewTodoStatus.VIEW_MODE, todos[2])
                )
            } else setViewVisibility(R.id.todo3, View.GONE)
            if (size >= 4) {
                setViewVisibility(R.id.todo4, View.VISIBLE)
                val todo = todos[3]
                val title = if (todo?.title != "") todo?.title else todo.note
                setTextViewText(R.id.tvTitle4, title)
                if (todo!!.alarmDate != null && todo.alarmDate!! > TimeUtil.currentTime())
                    setViewVisibility(R.id.imvClock4, View.VISIBLE)
                else
                    setViewVisibility(R.id.imvClock4, View.GONE)
                setOnClickPendingIntent(
                    R.id.todo4,
                    getPendingIntent(context, ViewTodoStatus.VIEW_MODE, todos[3])
                )
            } else setViewVisibility(R.id.todo4, View.GONE)
        }

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    private fun getPendingIntent(
        context: Context,
        mode: ViewTodoStatus,
        todo: Todo?
    ): PendingIntent {
        val argument = Bundle()
        argument.putSerializable(Const.VIEW_TODO_STATUS, mode)

        if (mode == ViewTodoStatus.VIEW_MODE) {
            argument.putInt(Const.ID_TODO_NEED_TO_VIEW, todo?.id ?: -1)
            return context.let {
                NavDeepLinkBuilder(it)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.viewTodoFragment)
                    .setArguments(argument)
                    .createPendingIntent()
            }
        }

        return context.let {
            NavDeepLinkBuilder(it)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.viewTodoFragment)
                .setArguments(argument)
                .createPendingIntent()
        }
    }
}

