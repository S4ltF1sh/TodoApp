package com.example.todo.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.adapters.forRV.TodoRVAdapter
import com.example.todo.data.MyDatabase
import com.example.todo.data.models.todo.Todo
import com.example.todo.databinding.FragmentGarbageBinding
import com.example.todo.common.Const
import com.example.todo.common.ItemsEditMode
import com.example.todo.common.TodoStatus
import com.example.todo.common.ViewTodoStatus
import com.example.todo.data.models.Item
import com.example.todo.data.repositories.GroupRepository
import com.example.todo.data.repositories.TodoRepository

class GarbageFragment(private val garbageBotNavListener: GarbageBotNavListener) : Fragment() {
    private lateinit var binding: FragmentGarbageBinding
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
        binding = FragmentGarbageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("GarbageFrag", "onViewCreated called! ")
        val todoDao = MyDatabase.getInstance(requireContext()).todoDao
        val groupDao = MyDatabase.getInstance(requireContext()).groupDao
        todoRepository = TodoRepository(todoDao)
        groupRepository = GroupRepository(groupDao)

        setContent()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setContent() {
        homeShareViewModel.updateGarbageFragmentData()
        homeShareViewModel.getGarbageFragmentLiveData().observe(viewLifecycleOwner) {
            binding.rvDeletedTodoList.adapter =
                TodoRVAdapter(it, viewTodo, restoreTodo, getEditMode, selectItem, unSelectItem)
        }
    }

    private val viewTodo = { todo: Todo ->
        val argument = Bundle()
        argument.putSerializable(Const.TODO_NEED_TO_VIEW, todo)
        argument.putSerializable(Const.VIEW_TODO_STATUS, ViewTodoStatus.VIEW_MODE)

        findNavController().navigate(R.id.action_homeFragment_to_viewTodoFragment, argument)
    }

    private val restoreTodo = { todo: Todo ->
        homeShareViewModel.addNewGroup(todo.groupName)
        homeShareViewModel.changeTodoStatus(todo.id, TodoStatus.ON_GOING)
        homeShareViewModel.updateOnGoingFragmentData()
        homeShareViewModel.updateGarbageFragmentData()
    }

    private val selectItem = { item: Item ->
        homeShareViewModel.selectItem(item)
        if (getEditMode() == ItemsEditMode.NONE) {
            homeShareViewModel.setEditMode(ItemsEditMode.ONLY_TODO_IN_GARBAGE)
            garbageBotNavListener.enterEditMode()
        }
    }

    private val unSelectItem = { item: Item ->
        homeShareViewModel.unSelectItem(item)
        if (homeShareViewModel.getSelectedItems().isEmpty()) {
            homeShareViewModel.setEditMode(ItemsEditMode.NONE)
            homeShareViewModel.unSelectAllItem()
            garbageBotNavListener.enterNavigationMode()
        }
    }

    private val getEditMode = { homeShareViewModel.getEditMode() }

    interface GarbageBotNavListener {
        fun enterEditMode()
        fun enterNavigationMode()
    }
}