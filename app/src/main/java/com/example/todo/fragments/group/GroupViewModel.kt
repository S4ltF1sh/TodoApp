package com.example.todo.fragments.group

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todo.common.ItemsEditMode
import com.example.todo.data.models.GroupWithTodos
import com.example.todo.data.models.todo.Todo
import com.example.todo.data.repositories.GroupRepository
import com.example.todo.data.repositories.TodoRepository
import com.example.todo.common.TodoStatus
import com.example.todo.data.models.Item
import com.example.todo.data.models.group.Group
import com.example.todo.utils.TimeUtil
import kotlinx.coroutines.launch
import java.util.*

class GroupViewModel(
    private val todoRepository: TodoRepository,
    private val groupRepository: GroupRepository,
    private val addRemind: (Todo) -> Unit,
    private val removeRemind: (Todo) -> Unit
) : ViewModel() {
    class GroupNeedToViewViewModelFactory(
        private val todoRepository: TodoRepository,
        private val groupRepository: GroupRepository,
        private val addRemind: (Todo) -> Unit,
        private val removeRemind: (Todo) -> Unit
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GroupViewModel(todoRepository, groupRepository, addRemind, removeRemind) as T
        }
    }

    private val groupWithTodosLiveData: MutableLiveData<GroupWithTodos> = MutableLiveData()
    private lateinit var groupWithTodos: GroupWithTodos
    private var editMode = ItemsEditMode.NONE
    private val selectedItems = mutableListOf<Item>()

    fun setData(title: String) {
        this.groupWithTodos =
            groupRepository.getGroupsWithTodosByGroupName(title)
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

    fun getEditMode() = editMode
    fun setEditMode(newEditMode: ItemsEditMode) {
        editMode = newEditMode
    }

    fun getSelectedItems() = selectedItems

    fun selectItem(item: Item) = selectedItems.add(item)

    fun unSelectItem(item: Item) = selectedItems.remove(item)

    fun unSelectAllItem() {
        selectedItems.clear()
    }

    fun remove1AllSelectedItems() {
        for (item in selectedItems) {
            if (item is Todo) {
                changeTodoStatus(item.id, TodoStatus.DELETED)
                removeRemind(item)
            }

        }
        unSelectAllItem()
        setEditMode(ItemsEditMode.NONE)
        updateData()
    }

    fun addAllSelectedTodosToGroup(groupName: String) {
        for (item in selectedItems) {
            if (item is Todo)
                addTodoToGroup(item.id, groupName)
        }

        unSelectAllItem()
        setEditMode(ItemsEditMode.NONE)
        updateData()
    }

    fun changeGroupName(newTitle: String) {
        val oldTitle = groupWithTodos.group!!.title
        groupRepository.updateGroupByTitle(oldTitle, newTitle, TimeUtil.currentTime())
        todoRepository.changeGroup2(oldTitle, newTitle)
        setData(newTitle)
    }

    private fun addTodoToGroup(id: Int, groupName: String) =
        todoRepository.changeGroup(id, groupName)

    fun checkDoneAllSelectedTodos() {
        for (item in selectedItems)
            if (item is Todo) {
                changeTodoStatus(item.id, TodoStatus.DONE)
                removeRemind(item)
            }

        unSelectAllItem()
        setEditMode(ItemsEditMode.NONE)
        updateData()
    }

    fun getAllGroup(): List<Group> = groupRepository.getAll()

    fun deleteGroupWithTodos() {
        val title = groupWithTodos.group!!.title
        val todos = groupWithTodos.todos

        for (todo: Todo in todos)
            removeRemind(todo)

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
    }

    fun addNewGroup(newGroupName: String) = viewModelScope.launch {
        if (newGroupName != "" && groupRepository.getGroupByTitle(newGroupName) == null)
            groupRepository.addGroup(Group(newGroupName, TimeUtil.currentTime()))
    }
}