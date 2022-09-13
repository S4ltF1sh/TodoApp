package com.example.todo.data

import android.content.Context
import androidx.room.*
import com.example.todo.data.models.group.Group
import com.example.todo.data.daos.GroupDao
import com.example.todo.data.models.todo.DateConverter
import com.example.todo.data.models.todo.Todo
import com.example.todo.data.daos.TodoDao
import com.example.todo.common.Const.TODO_DATABASE_NAME
import com.example.todo.data.models.OnGoingTodo


@Database(entities = [Todo::class, Group::class], views = [OnGoingTodo::class], version = 2)
@TypeConverters(DateConverter::class)
abstract class MyDatabase : RoomDatabase() {
    abstract val todoDao: TodoDao
    abstract val groupDao: GroupDao

    companion object {
        private var INSTANCE: MyDatabase? = null
        fun getInstance(context: Context): MyDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    TODO_DATABASE_NAME
                ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }

    }
}