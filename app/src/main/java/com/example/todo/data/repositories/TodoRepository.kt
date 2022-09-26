package com.example.todo.data.repositories

import com.example.todo.data.models.todo.Todo
import com.example.todo.data.daos.TodoDao
import com.example.todo.common.TodoStatus

class TodoRepository(private val todoDao: TodoDao) {
    fun addTodo(todo: Todo) = todoDao.add(todo)

    fun removeTodo(todo: Todo) = todoDao.removeTodo(todo)

    fun removeTodosFromGroup(
        currentTodoStatus: TodoStatus,
        currentGroupName: String,
        newTodoStatus: TodoStatus,
    ) = todoDao.deleteTodosFromGroup(
        currentTodoStatus,
        currentGroupName,
        newTodoStatus
    )


    fun getAll(): MutableList<Todo> =
        todoDao.getAll()

    fun getById(id: Int): Todo =
        todoDao.getById(id)

    fun getByTodoStatus(todoStatus: TodoStatus): List<Todo> =
        todoDao.getByTodoStatus(todoStatus)

    fun getTodosForOnGoingFragment(): List<Todo> =
        todoDao.getTodosForOnGoingFragment()

    fun getByKeyWord(keyWord: String): List<Todo> =
        todoDao.getByKeyWord(keyWord)

    fun updateTodo(todo: Todo) = todoDao.updateTodo(todo)

    fun changeTodoStatus(id: Int, newTodoStatus: TodoStatus) =
        todoDao.changeTodoStatus(id, newTodoStatus)

    fun changeGroup(id: Int, newGroupName: String) =
        todoDao.changeGroup(id, newGroupName)

    fun changeGroup2(oldGroupName: String, newGroupName: String) {
        todoDao.changeGroup2(oldGroupName, newGroupName)
    }
}