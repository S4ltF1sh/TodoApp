package com.example.todo.receivers

import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.example.todo.common.Const
import com.example.todo.common.TodoStatus
import com.example.todo.data.MyDatabase
import com.example.todo.widgets.TodoWidget

class ActionFromNotification : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Const.ACTION_CHECK_DONE_FROM_NOTIFICATION) {
            val id = intent.getIntExtra(Const.ID_TODO_NEED_TO_VIEW, 0)
            try {
                if (context != null) {
                    MyDatabase.getInstance(context).todoDao.changeTodoStatus(id, TodoStatus.DONE)
                    val notificationManager =
                        context.getSystemService(NotificationManager::class.java)
                    notificationManager.cancel(id)

                    updateWidget(context)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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