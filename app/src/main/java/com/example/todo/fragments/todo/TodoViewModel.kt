package com.example.todo.fragments.todo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todo.data.models.group.Group
import com.example.todo.data.models.todo.Todo
import com.example.todo.data.repositories.GroupRepository
import com.example.todo.data.repositories.TodoRepository
import com.example.todo.common.TodoStatus
import com.example.todo.utils.TimeUtil
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.*

class TodoViewModel(
    private val todoRepository: TodoRepository,
    private val groupRepository: GroupRepository
) :
    ViewModel() {
    class TodoNeedToViewViewModelFactory(
        private val todoRepository: TodoRepository,
        private val groupRepository: GroupRepository
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                return TodoViewModel(todoRepository, groupRepository) as T
            }

            throw IllegalArgumentException("Unable construct viewModel")
        }
    }

    private val todoLiveData: MutableLiveData<Todo> = MutableLiveData()
    private lateinit var todo: Todo

    fun setData(todo: Todo) {
        this.todo = todo
        todoLiveData.value = todo
    }

    fun setTodoStatus(todoStatus: TodoStatus) {
        todo.todoStatus = todoStatus
        todoLiveData.value = todo
    }

    fun setTime(alarmDate: Date?, editDate: Date?) {
        if (alarmDate != null && alarmDate > editDate)
            todo.alarmDate = alarmDate
        else
            todo.alarmDate = null
        todo.editDate = editDate
        todoLiveData.value = todo
    }

    fun setTitle(title: String) {
        todo.title = title
        todoLiveData.value = todo
    }

    fun setNote(note: String) {
        todo.note = note
        todoLiveData.value = todo
    }

    fun setGroupName(newGroupName: String) {
        todo.groupName = newGroupName
        todoLiveData.value = todo

        addNewGroup(newGroupName)
    }

    fun getTodoLiveData(): MutableLiveData<Todo> {
        todoLiveData.value = todo
        return todoLiveData
    }

    fun getTitle(): String = todo.title
    fun getNote(): String = todo.note
    fun getGroupName(): String = todo.groupName

    fun updateTodo() = viewModelScope.launch {
        todoRepository.updateTodo(todo)
    }

    fun addNewTodo(newTodo: Todo) {
        todoRepository.addTodo(newTodo)
        todo = newTodo
        todoLiveData.value = todo

        Log.d("id newTodo", newTodo.id.toString())
        Log.d("id current Todo", todo.id.toString())
    }

    fun delete2Todo() {
        Log.d("id current Todo", todo.id.toString())
        todoRepository.removeTodo(todo)
    }

    fun addNewGroup(newGroupName: String) {
        if (newGroupName != "" && groupRepository.getGroupByTitle(newGroupName) == null)
            groupRepository.addGroup(Group(newGroupName, TimeUtil.currentTime()))
    }
}