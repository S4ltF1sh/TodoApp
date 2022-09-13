package com.example.todo.data.models.todo

import androidx.room.*
import com.example.todo.data.models.Item
import com.example.todo.common.TodoStatus
import java.io.Serializable
import java.util.Date

@Entity(tableName = "todo_table")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo
    var title: String = "",
    @ColumnInfo
    var note: String = "",
    @ColumnInfo
    var alarmDate: Date? = null,
    @ColumnInfo
    var groupName: String = "",
    @ColumnInfo
    var todoStatus: TodoStatus = TodoStatus.ON_GOING,
    @ColumnInfo(name = "todo_edit_date")
    override var editDate: Date? = null,
) : Serializable, Item(editDate)
