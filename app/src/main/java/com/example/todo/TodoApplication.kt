package com.example.todo

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todo.common.Const
import com.example.todo.common.Const.CHANNEL_ID
import com.example.todo.data.MyDatabase
import com.example.todo.utils.Shortcuts

//Cần tạo cái channel ngay đầu ứng dụng để sử dụng nếu khônh muốn lỗi khi tạo thông báo đẩy :)
class TodoApplication : Application() {
    override fun onCreate() {
        //Từ Android 8 trở lên cần dùng Channel:
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val locationNotificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Location Notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            //locationNotificationChannel.setV

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(locationNotificationChannel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            Shortcuts.setUp(applicationContext)

        super.onCreate()
    }
}