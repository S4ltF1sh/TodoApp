package com.example.todo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.todo.common.Const.CHANNEL_ID
import com.example.todo.data.MyDatabase

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
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(locationNotificationChannel)
        }

        super.onCreate()
    }
}