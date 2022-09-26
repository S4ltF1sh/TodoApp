package com.example.todo.fragments.group

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.adapters.forRV.TodoRVAdapter
import com.example.todo.bottomSheets.AlertBottomSheet
import com.example.todo.bottomSheets.ChangeGroupNameBottomSheet
import com.example.todo.bottomSheets.CreateNewGroupBottomSheet
import com.example.todo.bottomSheets.GroupPickerBottomSheet
import com.example.todo.data.MyDatabase
import com.example.todo.data.models.todo.Todo
import com.example.todo.common.Const
import com.example.todo.common.ItemsEditMode
import com.example.todo.common.TodoStatus
import com.example.todo.utils.Toasts
import com.example.todo.common.ViewTodoStatus
import com.example.todo.data.models.Item
import com.example.todo.data.repositories.GroupRepository
import com.example.todo.data.repositories.TodoRepository
import com.example.todo.databinding.FragmentGroupBinding
import com.example.todo.utils.TimeUtil
import com.google.android.material.transition.MaterialContainerTransform

class GroupFragment : Fragment() {
    private lateinit var binding: FragmentGroupBinding
    private lateinit var todoRepository: TodoRepository
    private lateinit var groupRepository: GroupRepository
    private lateinit var adapter: TodoRVAdapter

    private val groupViewModel: GroupViewModel by lazy {
        ViewModelProvider(
            this,
            GroupViewModel.GroupNeedToViewViewModelFactory(
                todoRepository,
                groupRepository,
                addRemind,
                removeRemind
            )
        )[GroupViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigationButtonClicked()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val enterTransition = MaterialContainerTransform().apply {
            startContainerColor = requireContext().getColor(R.color.background_cardview)
            endContainerColor = requireContext().getColor(R.color.background_main)
            duration = 500
        }

        sharedElementEnterTransition = enterTransition
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val todoDao = MyDatabase.getInstance(requireContext()).todoDao
        val groupDao = MyDatabase.getInstance(requireContext()).groupDao
        todoRepository = TodoRepository(todoDao)
        groupRepository = GroupRepository(groupDao)

        setContent()
        setOnClick()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setContent() {
        adapter = TodoRVAdapter(
            viewTodo,
            checkDoneTodo,
            getEditMode,
            selectItem,
            unSelectItem
        )

        binding.rvTodoGroup.adapter = adapter

        groupViewModel.setData(arguments?.getString(Const.GROUP_TITLE_NEED_TO_VIEW) ?: "")
        groupViewModel.updateData()
        groupViewModel.getGroupWithTodosLiveData().observe(viewLifecycleOwner) {
            binding.tvTitle.text = it.group?.title
            val todos = it.todos.toMutableList().sortedBy { todo -> todo.editDate }
            adapter.setData(todos)
            if (todos.isNotEmpty())
                binding.tvShrugFace.visibility = View.GONE
            else
                binding.tvShrugFace.visibility = View.VISIBLE
        }

    }

    private fun setOnClick() {
        binding.apply {
            toolBar.setNavigationOnClickListener { navigationButtonClicked() }
            toolBar.setOnMenuItemClickListener(onMenuItemClickListener())
            botNav.setOnItemSelectedListener(onItemSelectedListener())
        }
    }

    private fun navigationButtonClicked() {
        if (getEditMode() == ItemsEditMode.NONE) {
            updateGroup()
            binding.toolBar.findNavController().popBackStack()
        } else {
            groupViewModel.unSelectAllItem()
            groupViewModel.setEditMode(ItemsEditMode.NONE)
            groupViewModel.updateData()
            enterNavigationMode()
        }
    }

    private fun updateGroup() {
        val editTime = TimeUtil.currentTime()
        val title = binding.tvTitle.text.toString().trim()
        groupViewModel.updateGroup(title, editTime)
    }

    private fun onMenuItemClickListener() = { item: MenuItem ->
        when (item.itemId) {
            R.id.itemDeleteGroup -> {
                val title = groupViewModel.getTitle()
                val size = groupViewModel.getTodos().size
                val buttonClicked = { choice: Boolean ->
                    if (choice) {
                        groupViewModel.deleteGroupWithTodos()
                        Toasts.deletedGroupToast(context, title, size)
                        findNavController().popBackStack()
                    }
                }

                AlertBottomSheet("Xoá $title và $size mục khác?", buttonClicked).show(
                    childFragmentManager,
                    Const.ALERT_BOTTOM_SHEET
                )
            }
            R.id.itemChangeGroupName -> {
                val changeTitle = { newTitle: String ->
                    binding.tvTitle.text = newTitle
                    groupViewModel.changeGroupName(newTitle)
                    arguments?.putString(Const.GROUP_TITLE_NEED_TO_VIEW, newTitle)
                    enterNavigationMode()
                }

                val oldTitle = binding.tvTitle.text.toString()
                ChangeGroupNameBottomSheet(oldTitle, changeTitle).show(
                    childFragmentManager,
                    Const.CHANGE_GROUP_TITLE
                )
            }
        }
        false
    }

    private fun onItemSelectedListener() = { item: MenuItem ->
        when (item.itemId) {
            R.id.itemDeleteAtBottom -> {
                val buttonClicked = { choice: Boolean ->
                    if (choice) {
                        groupViewModel.remove1AllSelectedItems()
                        enterNavigationMode()
                    }
                }

                AlertBottomSheet("Chuyển các mục đã chọn vào thùng rác?", buttonClicked).show(
                    childFragmentManager,
                    Const.ALERT_BOTTOM_SHEET
                )
            }
            R.id.itemCheckDoneAtBottom -> {
                groupViewModel.checkDoneAllSelectedTodos()
                enterNavigationMode()
            }
            R.id.itemSendToAtBottom -> {
                val groups = groupViewModel.getAllGroup()
                val setNewGroup = { newGroupName: String ->
                    if (newGroupName != binding.tvTitle.text.toString()) {
                        groupViewModel.addNewGroup(newGroupName)
                        groupViewModel.addAllSelectedTodosToGroup(newGroupName)
                        enterNavigationMode()
                    }
                }
                val openBottomSheetToCreateNewGroup = {
                    CreateNewGroupBottomSheet("", setNewGroup).show(
                        childFragmentManager,
                        Const.CREATE_NEW_GROUP_BOTTOM_SHEET
                    )
                }
                GroupPickerBottomSheet(
                    groups,
                    openBottomSheetToCreateNewGroup,
                    setNewGroup
                ).show(
                    childFragmentManager,
                    Const.GROUP_PICKER_BOTTOM_SHEET
                )
            }
        }
        true
    }

    private val viewTodo = { todo: Todo, extras: FragmentNavigator.Extras ->
        val argument = Bundle()
        argument.putInt(Const.ID_TODO_NEED_TO_VIEW, todo.id)
        argument.putSerializable(Const.VIEW_TODO_STATUS, ViewTodoStatus.VIEW_MODE)

        findNavController().navigate(
            R.id.action_viewGroupFragment_to_viewTodoFragment,
            argument,
            null,
            extras
        )
    }

    private val checkDoneTodo = { todo: Todo, position: Int ->
        groupViewModel.changeTodoStatus(todo.id, TodoStatus.DONE)
        removeRemind(todo)
        adapter.removeItem(position)
    }

    private val selectItem = { item: Item ->
        groupViewModel.selectItem(item)
        if (getEditMode() == ItemsEditMode.NONE) {
            groupViewModel.setEditMode(ItemsEditMode.ONLY_TODO_IN_GARBAGE)
            enterEditMode()
        }
    }

    private val unSelectItem = { item: Item ->
        groupViewModel.unSelectItem(item)
        if (groupViewModel.getSelectedItems().isEmpty()) {
            groupViewModel.setEditMode(ItemsEditMode.NONE)
            groupViewModel.unSelectAllItem()
            enterNavigationMode()
        }
    }

    private fun enterEditMode() {
        binding.toolBar.apply {
            menu.clear()
            setNavigationIcon(R.drawable.ic_cancel)
        }
        binding.botNav.visibility = View.VISIBLE
        notifyEditModeChange()
    }

    private fun enterNavigationMode() {
        binding.toolBar.apply {
            menu.clear()
            setNavigationIcon(R.drawable.ic_back)
            inflateMenu(R.menu.view_group)
        }
        binding.botNav.visibility = View.GONE
        binding.botNav.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_down_anim))
    }

    private fun notifyEditModeChange() {
        when (groupViewModel.getEditMode()) {
            ItemsEditMode.ONLY_TODOS -> {
                binding.botNav.apply {
                    menu.clear()
                    inflateMenu(R.menu.home_bottom_toolbar_only_todo_mode)
                }
            }
            else -> {}
        }
    }

    private val getEditMode = { groupViewModel.getEditMode() }

    private val addRemind = { todo: Todo ->
        (activity as MainActivity).addRemind(todo)
    }

    private val removeRemind = { todo: Todo ->
        (activity as MainActivity).removeRemind(todo)
    }
}