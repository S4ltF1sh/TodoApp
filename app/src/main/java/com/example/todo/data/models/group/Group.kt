package com.example.todo.data.models.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todo.data.models.Item
import java.io.Serializable
import java.util.*

@Entity(tableName = "group_table")
data class Group(
    @PrimaryKey var title: String = "",
    @ColumnInfo(name = "group_edit_date")
    override var editDate: Date?,
) : Serializable, Item(editDate)


