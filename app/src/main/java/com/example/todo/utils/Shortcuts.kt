package com.example.todo.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.example.todo.MainActivity
import com.example.todo.R

const val shortcut_create_todo_id = "id_todo"

object Shortcuts {
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun setUp(context: Context) {
        val shortcutManager =
            getSystemService(context, ShortcutManager::class.java)

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("todo://fragment/todo/View To Do Status"),
            context,
            MainActivity::class.java
        )

        val shortcutCreateTodo = ShortcutInfo.Builder(context, shortcut_create_todo_id)
            .setShortLabel("Việc cần làm")
            .setLongLabel("Tạo việc cần làm")
            .setIcon(Icon.createWithResource(context, R.drawable.ic_add))
            .setIntent(intent)
            .build()

        shortcutManager?.dynamicShortcuts = listOf(shortcutCreateTodo)
    }
}