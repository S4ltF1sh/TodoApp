package com.example.todo.fragments.group

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo.data.daos.GroupDao
import com.example.todo.data.models.GroupWithTodos
import com.example.todo.data.models.todo.Todo
import com.example.todo.data.daos.TodoDao
import com.example.todo.data.repositories.GroupRepository
import com.example.todo.data.repositories.TodoRepository
import com.example.todo.common.TodoStatus
import java.util.*

class GroupViewModel(todoDao: TodoDao, groupDao: GroupDao) : ViewModel() {
    class GroupNeedToViewViewModelFactory(
        private val todoDao: TodoDao,
        private val groupDao: GroupDao
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GroupViewModel(todoDao, groupDao) as T
        }
    }

    private val todoRepository: TodoRepository = TodoRepository(todoDao)
    private val groupRepository: GroupRepository = GroupRepository(groupDao)
    private val groupWithTodosLiveData: MutableLiveData<GroupWithTodos> = MutableLiveData()
    private lateinit var groupWithTodos: GroupWithTodos

    fun setData(groupWithTodos: GroupWithTodos) {
        this.groupWithTodos = groupWithTodos
        this.groupWithTodos =
            groupRepository.getGroupsWithTodosByGroupName(groupWithTodos.group!!.title)
        groupWithTodosLiveData.value = this.groupWithTodos
    }

    fun updateData() {
        this.groupWithTodos =
            groupRepository.getGroupsWithTodosByGroupName(groupWithTodos.group!!.title)
        groupWithTodosLiveData.value = this.groupWithTodos
    }

    fun updateGroup(newTitle: String, newEditDate: Date?) {
        val title = groupWithTodos.group!!.title
        groupRepository.updateGroupByTitle(title, newTitle, newEditDate)
    }

    fun getGroupWithTodosLiveData(): MutableLiveData<GroupWithTodos> = groupWithTodosLiveData
    fun getTitle(): String = groupWithTodos.group?.title ?: ""
    fun getTodos(): List<Todo> = groupWithTodos.todos

    fun deleteGroupWithTodos() {
        val title = groupWithTodos.group!!.title
        if (groupWithTodos.todos.isNotEmpty()) {
            todoRepository.removeTodosFromGroup(
                TodoStatus.ON_GOING,
                title,
                TodoStatus.DELETED,
            )
        }
        groupRepository.deleteGroupByTitle(title)
    }

    fun changeTodoStatus(id: Int, newTodoStatus: TodoStatus) {
        todoRepository.changeTodoStatus(id, newTodoStatus)
        updateData()
    }
}