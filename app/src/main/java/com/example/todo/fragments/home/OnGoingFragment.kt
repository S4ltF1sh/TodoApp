package com.example.todo.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.adapters.forRV.OnGoingFragmentRVAdapter
import com.example.todo.common.Const
import com.example.todo.common.Const.GROUP_NEED_TO_VIEW
import com.example.todo.common.ItemsEditMode
import com.example.todo.common.TodoStatus
import com.example.todo.common.ViewTodoStatus
import com.example.todo.data.MyDatabase
import com.example.todo.data.models.GroupWithTodos
import com.example.todo.data.models.Item
import com.example.todo.data.models.todo.Todo
import com.example.todo.data.repositories.GroupRepository
import com.example.todo.data.repositories.TodoRepository
import com.example.todo.databinding.FragmentOnGoingBinding

class OnGoingFragment(
    onGoingBotNavListener: OnGoingBotNavListener,
) : Fragment() {
    private lateinit var binding: FragmentOnGoingBinding
    private lateinit var todoRepository: TodoRepository
    private lateinit var groupRepository: GroupRepository
    private val homeShareViewModel: HomeShareViewModel by lazy {
        ViewModelProvider(
            requireParentFragment(),
            HomeShareViewModel.HomeShareViewModelFactory(todoRepository, groupRepository)
        )[HomeShareViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnGoingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val todoDao = MyDatabase.getInstance(requireContext()).todoDao
        val groupDao = MyDatabase.getInstance(requireContext()).groupDao
        todoRepository = TodoRepository(todoDao)
        groupRepository = GroupRepository(groupDao)

        homeShareViewModel.updateOnGoingFragmentData()
        homeShareViewModel.getOnGoingFragmentLiveData().observe(viewLifecycleOwner) {
            binding.rvItemList.adapter =
                OnGoingFragmentRVAdapter(
                    it,
                    viewTodo,
                    viewGroup,
                    getEditMode,
                    checkDoneTodo,
                    addTodoToGroup,
                    selectItem,
                    unSelectItem,
                )
        }

        setOnClick()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setOnClick() {
        binding.fabAdd.setOnClickListener {
            addNewTodo(R.id.action_homeFragment_to_viewTodoFragment)
        }
    }

    private fun addNewTodo(action: Int) {
        val argument = Bundle()
        argument.putSerializable(Const.VIEW_TODO_STATUS, ViewTodoStatus.ADD_MODE)

        binding.fabAdd.findNavController().navigate(action, argument)
    }

    private val viewTodo = { todo: Todo ->
        if (homeShareViewModel.getEditMode() == ItemsEditMode.NONE) {
            val argument = Bundle()
            argument.putSerializable(Const.TODO_NEED_TO_VIEW, todo)
            argument.putSerializable(Const.VIEW_TODO_STATUS, ViewTodoStatus.VIEW_MODE)

            findNavController().navigate(R.id.action_homeFragment_to_viewTodoFragment, argument)
        }
    }

    private val viewGroup = { groupWithTodos: GroupWithTodos ->
        if (homeShareViewModel.getEditMode() == ItemsEditMode.NONE) {
            val argument = Bundle()
            argument.putSerializable(GROUP_NEED_TO_VIEW, groupWithTodos)

            findNavController().navigate(R.id.action_homeFragment_to_viewGroupFragment, argument)
        }
    }

    private val getEditMode = { homeShareViewModel.getEditMode() }

    private val checkDoneTodo = { todo: Todo ->
        homeShareViewModel.changeTodoStatus(todo.id, TodoStatus.DONE)
        homeShareViewModel.updateOnGoingFragmentData()
        homeShareViewModel.updateGarbageFragmentData()
    }

    private val addTodoToGroup = { id: Int, groupName: String ->
        homeShareViewModel.addTodoToGroup(id, groupName)
        Toast.makeText(context, "Đã thêm 1 việc cần làm vào nhóm $groupName!", Toast.LENGTH_SHORT)
            .show()
    }

    private val selectItem = { item: Item ->
        homeShareViewModel.selectItem(item)
        if (getEditMode() == ItemsEditMode.NONE) {
            when (item) {
                is Todo -> homeShareViewModel.setEditMode(ItemsEditMode.ONLY_TODOS)
                is GroupWithTodos -> homeShareViewModel.setEditMode(ItemsEditMode.ONLY_ONE_GROUP)
            }
            onGoingBotNavListener.enterEditMode()
        } else if ((getEditMode() == ItemsEditMode.ONLY_TODOS && item is GroupWithTodos) ||
            (getEditMode() == ItemsEditMode.ONLY_ONE_GROUP)
        ) {
            homeShareViewModel.setEditMode(ItemsEditMode.WITH_GROUP)
            onGoingBotNavListener.notifyEditModeChange()
        }
    }

    private val unSelectItem = { item: Item ->
        homeShareViewModel.unSelectItem(item)
        val selectedItems = homeShareViewModel.getSelectedItems()
        if (selectedItems.isEmpty()) {
            homeShareViewModel.setEditMode(ItemsEditMode.NONE)
            homeShareViewModel.unSelectAllItem()
            onGoingBotNavListener.enterNavigationMode()
        } else if (item is GroupWithTodos && !selectedItems.any { it is GroupWithTodos }) {
            homeShareViewModel.setEditMode(ItemsEditMode.ONLY_TODOS)
            onGoingBotNavListener.notifyEditModeChange()
        } else if (selectedItems.size == 1 && selectedItems[0] is GroupWithTodos) {
            homeShareViewModel.setEditMode(ItemsEditMode.ONLY_ONE_GROUP)
            onGoingBotNavListener.notifyEditModeChange()
        }
    }

    interface OnGoingBotNavListener {
        fun enterEditMode()
        fun enterNavigationMode()
        fun notifyEditModeChange()
    }
}