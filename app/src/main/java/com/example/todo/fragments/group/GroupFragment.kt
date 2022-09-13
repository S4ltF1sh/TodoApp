package com.example.todo.fragments.group

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.adapters.forRV.TodoRVAdapter
import com.example.todo.data.MyDatabase
import com.example.todo.data.daos.GroupDao
import com.example.todo.data.models.GroupWithTodos
import com.example.todo.data.models.todo.Todo
import com.example.todo.data.daos.TodoDao
import com.example.todo.common.Const
import com.example.todo.common.Const.GROUP_NEED_TO_VIEW
import com.example.todo.common.ItemsEditMode
import com.example.todo.common.TodoStatus
import com.example.todo.utils.Toasts
import com.example.todo.common.ViewTodoStatus
import com.example.todo.data.models.Item
import com.example.todo.databinding.FragmentGroupBinding
import com.example.todo.utils.TimeUtil

class GroupFragment : Fragment() {
    private lateinit var binding: FragmentGroupBinding
    private lateinit var todoDao: TodoDao
    private lateinit var groupDao: GroupDao
    private val groupViewModel: GroupViewModel by lazy {
        ViewModelProvider(
            this,
            GroupViewModel.GroupNeedToViewViewModelFactory(todoDao, groupDao)
        )[GroupViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backButtonClicked()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        todoDao = MyDatabase.getInstance(requireContext()).todoDao
        groupDao = MyDatabase.getInstance(requireContext()).groupDao
        setContent()
        setOnClick()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setContent() {
        groupViewModel.setData(arguments?.get(GROUP_NEED_TO_VIEW) as GroupWithTodos)
        groupViewModel.getGroupWithTodosLiveData().observe(viewLifecycleOwner) {
            binding.tvTitle.text = it.group?.title
            val todos = it.todos.toMutableList().sortedBy { todo -> todo.editDate }
            binding.rvTodoGroup.adapter = TodoRVAdapter(
                todos.reversed(),
                viewTodo,
                checkDoneTodo,
                getEditMode,
                selectItem,
                unSelectItem
            )
        }

    }

    private fun setOnClick() {
        binding.apply {
            toolBar.setNavigationOnClickListener { backButtonClicked() }
            toolBar.setOnMenuItemClickListener(onMenuItemClickListener())
        }
    }

    private fun backButtonClicked() {
        updateGroup()
        binding.toolBar.findNavController().popBackStack()
    }

    private fun updateGroup() {
        val editTime = TimeUtil.currentTime()
        val title = binding.tvTitle.text.toString().trim()
        groupViewModel.updateGroup(title, editTime)
    }

    private fun onMenuItemClickListener() = { item: MenuItem ->
        when (item.itemId) {
            R.id.itemDeleteGroup -> {
                Toasts.deletedGroupToast(
                    context,
                    groupViewModel.getTitle(),
                    groupViewModel.getTodos().size
                )
                groupViewModel.deleteGroupWithTodos()

                findNavController().popBackStack()
            }
            R.id.itemChangeGroupName -> {}
        }
        false
    }

    private val viewTodo = { todo: Todo ->
        val argument = Bundle()
        argument.putSerializable(Const.TODO_NEED_TO_VIEW, todo)
        argument.putSerializable(Const.VIEW_TODO_STATUS, ViewTodoStatus.VIEW_MODE)

        findNavController().navigate(R.id.action_viewGroupFragment_to_viewTodoFragment, argument)
    }

    private val checkDoneTodo = { todo: Todo ->
        groupViewModel.changeTodoStatus(todo.id, TodoStatus.DONE)
    }

    private val selectItem = { item: Item ->
//        homeShareViewModel.selectItem(item)
//        if (getEditMode() == ItemsEditMode.NONE) {
//            homeShareViewModel.setEditMode(ItemsEditMode.ONLY_TODO_IN_GARBAGE)
//            garbageBotNavListener.enterEditMode()
//        }
    }

    private val unSelectItem = { item: Item ->
//        homeShareViewModel.unSelectItem(item)
//        if (homeShareViewModel.getSelectedItems().isEmpty()) {
//            homeShareViewModel.setEditMode(ItemsEditMode.NONE)
//            homeShareViewModel.unSelectAllItem()
//            garbageBotNavListener.enterNavigationMode()
//        }
    }

    private val getEditMode = { ItemsEditMode.ONLY_TODO_IN_GROUP }

    interface GarbageBotNavListener {
        fun enterEditMode()
        fun enterNavigationMode()
    }
}