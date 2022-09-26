package com.example.todo.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.example.todo.MainActivity
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
    private lateinit var adapter: TodoRVAdapter

    private val homeShareViewModel: HomeShareViewModel by lazy {
        ViewModelProvider(
            requireParentFragment(),
            HomeShareViewModel.HomeShareViewModelFactory(
                todoRepository,
                groupRepository,
                addRemind,
                removeRemind
            )
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
        adapter = TodoRVAdapter(viewTodo, restoreTodo, getEditMode, selectItem, unSelectItem)
        binding.rvDeletedTodoList.adapter = adapter
        homeShareViewModel.updateGarbageFragmentData()
        homeShareViewModel.getGarbageFragmentLiveData().observe(viewLifecycleOwner) {
            adapter.setData(it)
            if (it.isNotEmpty())
                binding.tvShrugFace.visibility = View.GONE
            else
                binding.tvShrugFace.visibility = View.VISIBLE
        }
    }

    private val viewTodo = { todo: Todo, extras: FragmentNavigator.Extras ->
        val argument = Bundle()
        argument.putInt(Const.ID_TODO_NEED_TO_VIEW, todo.id)
        argument.putSerializable(Const.VIEW_TODO_STATUS, ViewTodoStatus.VIEW_MODE)

        try {
            findNavController().navigate(
                R.id.action_homeFragment_to_viewTodoFragment,
                argument,
                null,
                extras
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private val restoreTodo = { todo: Todo, position: Int ->
        homeShareViewModel.addNewGroup(todo.groupName)
        homeShareViewModel.changeTodoStatus(todo.id, TodoStatus.ON_GOING)
        homeShareViewModel.updateOnGoingFragmentData()
        addRemind(todo)
        updateWidgets()
        adapter.removeItem(position)
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

    private val addRemind = { todo: Todo ->
        (activity as MainActivity).addRemind(todo)
    }
    private val removeRemind = { _: Todo -> }

    private fun updateWidgets() {
        (activity as MainActivity).updateWidgets()
    }

    interface GarbageBotNavListener {
        fun enterEditMode()
        fun enterNavigationMode()
    }
}