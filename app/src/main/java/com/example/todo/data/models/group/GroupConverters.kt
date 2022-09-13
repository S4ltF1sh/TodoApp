package com.example.todo.data.models.group

import androidx.room.TypeConverter
import com.example.todo.data.models.todo.Todo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GroupConverters {
    @TypeConverter
    fun fromTodoList(list: List<Int>): String? = Gson().toJson(list)

    @TypeConverter
    fun toTodoList(string: String): List<Todo>? {
        val type = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(string, type)
    }
}