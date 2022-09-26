package com.example.todo.data.daos

import androidx.room.*
import com.example.todo.data.models.todo.Todo
import com.example.todo.common.TodoStatus

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_table WHERE title LIKE '%'||:title||'%'")
    fun getByKeyWord(title: String): List<Todo>

    @Query("SELECT * FROM todo_table WHERE id = :id")
    fun getById(id: Int): Todo

    @Query("SELECT * FROM todo_table WHERE todoStatus = :todoStatus")
    fun getByTodoStatus(todoStatus: TodoStatus): List<Todo>

    @Query("SELECT * FROM todo_table WHERE todoStatus = 'ON_GOING' AND groupName = ''")
    fun getTodosForOnGoingFragment(): List<Todo>

    @Query("SELECT * FROM todo_table")
    fun getAll(): MutableList<Todo>

    @Query("UPDATE todo_table SET todoStatus = :newTodoStatus WHERE todoStatus = :currentTodoStatus AND groupName = :currentGroupName")
    fun deleteTodosFromGroup(
        currentTodoStatus: TodoStatus,
        currentGroupName: String,
        newTodoStatus: TodoStatus,
    )

    @Query("UPDATE todo_table SET todoStatus = :newTodoStatus WHERE id = :id")
    fun changeTodoStatus(id: Int, newTodoStatus: TodoStatus)

    @Query("UPDATE todo_table SET groupName = :newGroupName WHERE id = :id")
    fun changeGroup(id: Int, newGroupName: String)

    @Query("UPDATE todo_table SET groupName = :newGroupName WHERE groupName = :oldGroupName")
    fun changeGroup2(oldGroupName: String, newGroupName: String)

    @Update
    fun updateTodo(todo: Todo)

    @Delete
    fun removeTodo(todo: Todo)

    @Insert
    fun add(todo: Todo)
}