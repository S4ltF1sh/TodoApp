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
    private val groupRepository: GroupRepository,
    private val addRemind: (Todo) -> Unit,
    private val removeRemind: (Todo) -> Unit
) : ViewModel() {
    companion object {
        val onGoingFragmentLiveData: MutableLiveData<List<Item>?> = MutableLiveData<List<Item>?>()
        val garbageFragmentLiveData: MutableLiveData<List<Todo>?> = MutableLiveData<List<Todo>?>()
    }

    private val items = mutableListOf<Item>()
    private val todos = mutableListOf<Todo>()
    private var editMode = ItemsEditMode.NONE
    private val selectedItems = mutableListOf<Item>()

    class HomeShareViewModelFactory(
        private val todoRepository: TodoRepository,
        private val groupRepository: GroupRepository,
        private val addRemind: (Todo) -> Unit,
        private val removeRemind: (Todo) -> Unit
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeShareViewModel::class.java)) {
                return HomeShareViewModel(
                    todoRepository,
                    groupRepository,
                    addRemind,
                    removeRemind
                ) as T
            }

            throw IllegalArgumentException("Unable construct viewModel")
        }
    }

    fun getEditMode() = editMode
    fun setEditMode(newEditMode: ItemsEditMode) {
        editMode = newEditMode
    }

    //GoingFragment:
    fun getOnGoingFragmentLiveData(): MutableLiveData<List<Item>?> = onGoingFragmentLiveData

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
                is Todo -> {
                    changeTodoStatus(item.id, TodoStatus.DELETED)
                    removeRemind(item)
                }
                is GroupWithTodos -> {
                    for (todo: Todo in item.todos)
                        removeRemind(todo)
                    deleteGroupWithTodos(item)
                }
            }
        }
        unSelectAllItem()
        setEditMode(ItemsEditMode.NONE)
        updateOnGoingFragmentData()
        updateGarbageFragmentData()
    }

    fun checkDoneAllSelectedTodos() {
        for (item in selectedItems)
            if (item is Todo) {
                changeTodoStatus(item.id, TodoStatus.DONE)
                removeRemind(item)
            }

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
        if (groupName != "") {
            for (item in selectedItems) {
                if (item is Todo)
                    addTodoToGroup(item.id, groupName)
            }

            unSelectAllItem()
            setEditMode(ItemsEditMode.NONE)
            updateOnGoingFragmentData()
        }
    }

    //GarbageFragment:
    fun getGarbageFragmentLiveData(): MutableLiveData<List<Todo>?> = garbageFragmentLiveData

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
            if (item is Todo) {
                addNewGroup(item.groupName)
                changeTodoStatus(item.id, TodoStatus.ON_GOING)
                addRemind(item)
            }

        unSelectAllItem()
        setEditMode(ItemsEditMode.NONE)
        updateOnGoingFragmentData()
        updateGarbageFragmentData()
    }

    fun changeGroupName(newTitle: String) {
        if (selectedItems.size == 1) {
            val oldTitle = (selectedItems[0] as GroupWithTodos).group?.title ?: "Nhóm mặc định"
            groupRepository.updateGroupByTitle(oldTitle, newTitle, TimeUtil.currentTime())
            todoRepository.changeGroup2(oldTitle, newTitle)
            unSelectAllItem()
            setEditMode(ItemsEditMode.NONE)
            updateOnGoingFragmentData()
        }
    }

    private fun getTodosForOnGoingFragment() =
        todoRepository.getTodosForOnGoingFragment()

    private fun getTodoByTodoStatus(todoStatus: TodoStatus) =
        todoRepository.getByTodoStatus(todoStatus)

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

    fun getAllGroup(): List<Group> = groupRepository.getAll()

    override fun onCleared() {
        items.clear()
        todos.clear()
        selectedItems.clear()
        onGoingFragmentLiveData.value = null
        garbageFragmentLiveData.value = null
        super.onCleared()
    }
}