package com.example.todo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.todo.common.Const.ACTION_VIEW_TODO_FROM_WIDGET
import com.example.todo.data.models.todo.Todo
import com.example.todo.databinding.ActivityMainBinding
import com.example.todo.receivers.RemindReceiver
import com.example.todo.utils.TimeUtil
import com.example.todo.widgets.TodoWidget

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
    }

    fun addRemind(todo: Todo) {
        val alarmDate = todo.alarmDate
        if (alarmDate != null && alarmDate.time > TimeUtil.currentTime()!!.time) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmDate.time,
                getRemindPendingIntent(getRemindIntent(todo))
            )
        }
    }

    fun removeRemind(todo: Todo) {
        alarmManager.cancel(getRemindPendingIntent(getRemindIntent(todo)))
    }

    @SuppressLint("NewApi")
    private fun getRemindIntent(todo: Todo): Intent {
        val intent = Intent(this, RemindReceiver::class.java)
        intent.action = ACTION_VIEW_TODO_FROM_WIDGET
        intent.flags = todo.id
        intent.putExtra("title", todo.title)
        intent.putExtra("note", todo.note)
        intent.putExtra("id", todo.id)
        return intent
    }

    private fun getRemindPendingIntent(intent: Intent): PendingIntent {
        return PendingIntent.getBroadcast(this, intent.flags, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onDestroy() {
        Log.d("MainActivity", "onDestroy")
        updateWidgets()
        super.onDestroy()
    }

    fun updateWidgets() {
        val intent = Intent(this, TodoWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val ids = AppWidgetManager.getInstance(this).getAppWidgetIds(
            ComponentName(
                this,
                TodoWidget::class.java
            )
        )
        if (ids != null && ids.isNotEmpty()) {
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            this.sendBroadcast(intent)
        }
    }
}


