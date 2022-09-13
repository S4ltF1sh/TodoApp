package com.example.todo.data.models

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Relation
import com.example.todo.data.models.group.Group
import com.example.todo.data.models.todo.Todo
import java.io.Serializable

@DatabaseView("SELECT * FROM todo_table WHERE todoStatus = 'ON_GOING'")
data class OnGoingTodo(
    @Embedded val todo: Todo
)

data class GroupWithTodos(
    @Embedded val group: Group?,
    @Relation(
        parentColumn = "title",
        entityColumn = "groupName",
        entity = OnGoingTodo::class
    )
    val todos: List<Todo>,
) : Serializable, Item(group?.editDate)
