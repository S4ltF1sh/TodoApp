package com.example.todo.fragments.home

import androidx.lifecycle.*
import com.example.todo.common.ItemsEditMode
import com.example.todo.common.TodoStatus
import com.example.todo.data.models.GroupWithTodos
import com.example.todo.data.models.Item
import com.example.todo.data.models.group.Group
import com.example.todo.data.models.todo.Todo
import com.example.todo.data.repositories.TodoRepository
import com.example.todo.data.repositories.GroupRepository
import com.example.todo.utils.TimeUtil
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class HomeShareViewModel(
    private val todoRepository: TodoRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val onGoingFragmentLiveData: MutableLiveData<List<Item>> = MutableLiveData<List<Item>>()
    private val garbageFragmentLiveData: MutableLiveData<List<Todo>> = MutableLiveData<List<Todo>>()
    private val items = mutableListOf<Item>()
    private val todos = mutableListOf<Todo>()
    private var editMode = ItemsEditMode.NONE
    private val selectedItems = mutableListOf<Item>()

    class HomeShareViewModelFactory(
        private val todoRepository: TodoRepository,
        private val groupRepository: GroupRepository
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeShareViewModel::class.java)) {
                return HomeShareViewModel(todoRepository, groupRepository) as T
            }

            throw IllegalArgumentException("Unable construct viewModel")
        }
    }

    fun getEditMode() = editMode
    fun setEditMode(newEditMode: ItemsEditMode) {
        editMode = newEditMode
    }

    //GoingFragment:
    fun getOnGoingFragmentLiveData(): LiveData<List<Item>> = onGoingFragmentLiveData

    fun updateOnGoingFragmentData() {
        items.clear()
        items.addAll(getTodosForOnGoingFragment())
        getGroupsWithTodos()?.let { items.addAll(it) }
        onGoingFragmentLiveData.value = items.sortedBy { it.editDate }.reversed()
    }

    fun getSelectedItems() = selectedItems

    fun selectItem(item: Item) = selectedItems.add(item)

    fun unSelectItem(item: Item) = selectedItems.remove(item)

    fun unSelectAllItem() {
        selectedItems.clear()
    }

    fun remove1AllSelectedItems() {
        for (item in selectedItems) {
            when (item) {
                is Todo -> changeTodoStatus(item.id, TodoStatus.DELETED)
                is GroupWithTodos -> deleteGroupWithTodos(item)
            }
        }
        unSelectAllItem()
        setEditMode(ItemsEditMode.NONE)
        updateOnGoingFragmentData()
        updateGarbageFragmentData()
    }

    fun checkDoneAllSelectedTodos() {
        for (item in selectedItems)
            if (item is Todo)
                changeTodoStatus(item.id, TodoStatus.DONE)

        unSelectAllItem()
        setEditMode(ItemsEditMode.NONE)
        updateOnGoingFragmentData()
        updateGarbageFragmentData()
    }

    private fun deleteGroupWithTodos(groupWithTodos: GroupWithTodos) {
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

    fun addAllSelectedTodosToGroup(groupName: String) {
        for (item in items) {
            if (item is Todo)
                addTodoToGroup(item.id, groupName)
        }
        updateOnGoingFragmentData()
    }

    //GarbageFragment:
    fun getGarbageFragmentLiveData(): LiveData<List<Todo>> = garbageFragmentLiveData

    fun updateGarbageFragmentData() {
        todos.clear()
        todos.addAll(getTodoByTodoStatus(TodoStatus.DELETED))
        todos.addAll(getTodoByTodoStatus(TodoStatus.DONE))
        garbageFragmentLiveData.value = todos.sortedBy { it.editDate }.reversed()
    }

    fun remove2AllSelectedTodos() {
        for (item in selectedItems)
            if (item is Todo)
                todoRepository.removeTodo(item)
        unSelectAllItem()
        setEditMode(ItemsEditMode.NONE)
        updateGarbageFragmentData()
    }

    fun restoreAllSelectedTodos() {
        for (item in selectedItems)
            if (item is Todo)
                changeTodoStatus(item.id, TodoStatus.ON_GOING)

        unSelectAllItem()
        setEditMode(ItemsEditMode.NONE)
        updateOnGoingFragmentData()
        updateGarbageFragmentData()
    }

    private fun getTodosForOnGoingFragment() =
        todoRepository.getTodosForOnGoingFragment()

    private fun getTodoByTodoStatus(todoStatus: TodoStatus) =
        todoRepository.getByTodoStatus(todoStatus)

    fun searchTodo(keyWord: String) =
        todoRepository.getByKeyWord(keyWord)

    fun changeTodoStatus(id: Int, newTodoStatus: TodoStatus) {
        todoRepository.changeTodoStatus(id, newTodoStatus)
    }

    //Group:
    fun addNewGroup(newGroupName: String) = viewModelScope.launch {
        if (newGroupName != "" && groupRepository.getGroupByTitle(newGroupName) == null)
            groupRepository.addGroup(Group(newGroupName, TimeUtil.currentTime()))
    }

    private fun getGroupsWithTodos() = groupRepository.getGroupsWithTodos()

    fun addTodoToGroup(id: Int, groupName: String) {
        todoRepository.changeGroup(id, groupName)
        updateOnGoingFragmentData()
    }
}