package com.example.todo.receivers

import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.todo.common.Const
import com.example.todo.common.TodoStatus
import com.example.todo.data.MyDatabase
import com.example.todo.data.models.Item
import com.example.todo.data.models.todo.Todo
import com.example.todo.fragments.home.HomeShareViewModel
import com.example.todo.widgets.TodoWidget

class ActionFromNotification : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Const.ACTION_CHECK_DONE_FROM_NOTIFICATION) {
            val id = intent.getIntExtra(Const.ID_TODO_NEED_TO_VIEW, 0)
            try {
                if (context != null) {
                    MyDatabase.getInstance(context.applicationContext).todoDao.changeTodoStatus(
                        id,
                        TodoStatus.DONE
                    )
                    val notificationManager =
                        context.getSystemService(NotificationManager::class.java)
                    notificationManager.cancel(id)

                    updateWidget(context)
                    updateHomeFragment(context)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateHomeFragment(context: Context) {
        val todoDao = MyDatabase.getInstance(context).todoDao
        val groupDao = MyDatabase.getInstance(context).groupDao
        val onGoingData: MutableList<Item> = mutableListOf()
        val garbageData: MutableList<Todo> = mutableListOf()
        onGoingData.addAll(todoDao.getTodosForOnGoingFragment())
        groupDao.getGroupsWithTodos()?.let { onGoingData.addAll(it) }
        garbageData.addAll(todoDao.getByTodoStatus(TodoStatus.DONE))
        garbageData.addAll(todoDao.getByTodoStatus(TodoStatus.DELETED))

        HomeShareViewModel.onGoingFragmentLiveData.value = onGoingData
        HomeShareViewModel.garbageFragmentLiveData.value = garbageData
        Log.i("Nothing", "updateHomeFragment: ${HomeShareViewModel.onGoingFragmentLiveData}")
    }

    private fun updateWidget(context: Context) {
        val intent = Intent(context, TodoWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
            ComponentName(
                context,
                TodoWidget::class.java
            )
        )
        if (ids != null && ids.isNotEmpty()) {
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }

}