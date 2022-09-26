package com.example.todo.receivers

import com.example.todo.R
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.todo.common.Const
import com.example.todo.common.Const.ACTION_VIEW_TODO_FROM_WIDGET
import com.example.todo.common.Const.CHANNEL_ID
import com.example.todo.common.TodoStatus
import com.example.todo.common.ViewTodoStatus
import com.example.todo.data.MyDatabase


@RequiresApi(Build.VERSION_CODES.O)
class RemindReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_VIEW_TODO_FROM_WIDGET) {
            val id = intent.getIntExtra("id", -1)
            val title = intent.getStringExtra("title")!!
            val note = intent.getStringExtra("note")!!

            sendPushNotification(title, note, id, context)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag", "ResourceType")
    private fun sendPushNotification(title: String, note: String, id: Int, context: Context?) {
        val argument = Bundle()
        argument.putInt(Const.ID_TODO_NEED_TO_VIEW, id)
        argument.putSerializable(Const.VIEW_TODO_STATUS, ViewTodoStatus.VIEW_MODE)

        val pendingIntent = context?.let {
            NavDeepLinkBuilder(it)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.viewTodoFragment)
                .setArguments(argument)
                .createPendingIntent()
        }

        val intent = Intent(context, ActionFromNotification::class.java).apply {
            action = Const.ACTION_CHECK_DONE_FROM_NOTIFICATION
            putExtra(Const.ID_TODO_NEED_TO_VIEW, id)
        }

        val checkDonePendingIntent =
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE)

        val layout = RemoteViews(context?.packageName, R.layout.notification)
        layout.setOnClickPendingIntent(R.id.btnNotificationCheckDone, checkDonePendingIntent)
        layout.setTextViewText(R.id.tvNotificationTitle, title)
        layout.setTextViewText(R.id.tvNotificationNote, note)

        val notification = context?.let {
            NotificationCompat.Builder(it, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(note)
                .setSmallIcon(R.drawable.ic_alarm_24)
                .setCustomBigContentView(layout)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .build()
        }

        val notificationManager =
            context?.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, notification)
    }
}