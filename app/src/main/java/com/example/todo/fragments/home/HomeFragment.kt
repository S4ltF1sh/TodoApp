package com.example.todo.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.adapters.forViewPager2.HomeFragmentViewPager2Adapter
import com.example.todo.bottomSheets.AlertBottomSheet
import com.example.todo.bottomSheets.ChangeGroupNameBottomSheet
import com.example.todo.bottomSheets.CreateNewGroupBottomSheet
import com.example.todo.bottomSheets.GroupPickerBottomSheet
import com.example.todo.common.Const
import com.example.todo.common.Const.GROUP_PICKER_BOTTOM_SHEET
import com.example.todo.common.ItemsEditMode
import com.example.todo.data.MyDatabase
import com.example.todo.data.models.todo.Todo
import com.example.todo.data.repositories.GroupRepository
import com.example.todo.data.repositories.TodoRepository
import com.example.todo.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment(), OnGoingFragment.OnGoingBotNavListener,
    GarbageFragment.GarbageBotNavListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var todoRepository: TodoRepository
    private lateinit var groupRepository: GroupRepository

    private val homeShareViewModel: HomeShareViewModel by lazy {
        ViewModelProvider(
            this,
            HomeShareViewModel.HomeShareViewModelFactory(
                todoRepository,
                groupRepository,
                addRemind,
                removeRemind
            )
        )[HomeShareViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("HomeFrag", "onCreate: ")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("HomeFrag", "onCreateView: ")
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val todoDao = MyDatabase.getInstance(requireContext()).todoDao
        val groupDao = MyDatabase.getInstance(requireContext()).groupDao
        todoRepository = TodoRepository(todoDao)
        groupRepository = GroupRepository(groupDao)
        Log.d("HomeFrag", "onViewCreated: ")
        setupTabLayout()
        setupSearchView()
        setupBotNav()
        setOnClick()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupBotNav() {
        binding.botNav.itemActiveIndicatorColor = null
    }

    private fun setupTabLayout() {
        binding.pager.adapter =
            HomeFragmentViewPager2Adapter(childFragmentManager, lifecycle, this, this)

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.ic_group_note)
                1 -> tab.setIcon(R.drawable.ic_delete)
            }
        }.attach()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchView() {
        binding.edtSearchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.toolBar.visibility = View.GONE
                binding.pager.isUserInputEnabled = false
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            } else {
                binding.toolBar.visibility = View.VISIBLE
                binding.pager.isUserInputEnabled = true
            }
        }
    }

    private fun setOnClick() {
        binding.apply {
            toolBar.setNavigationOnClickListener { navigationButtonClicked() }
            botNav.setOnItemSelectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.itemDeleteAtBottom -> {
                        val buttonClicked = { choice: Boolean ->
                            if (choice) {
                                homeShareViewModel.remove1AllSelectedItems()
                                enterNavigationMode()
                                updateWidgets()
                            }
                        }

                        AlertBottomSheet("Chuyển các mục đã chọn vào thùng rác?", buttonClicked).show(
                            childFragmentManager,
                            Const.ALERT_BOTTOM_SHEET
                        )
                    }
                    R.id.itemSendToAtBottom -> {
                        val groups = homeShareViewModel.getAllGroup()
                        val setNewGroup = { newGroupName: String ->
                            if (newGroupName != "") {
                                homeShareViewModel.addNewGroup(newGroupName)
                                homeShareViewModel.addAllSelectedTodosToGroup(newGroupName)
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
                            GROUP_PICKER_BOTTOM_SHEET
                        )
                    }
                    R.id.itemCheckDoneAtBottom -> {
                        homeShareViewModel.checkDoneAllSelectedTodos()
                        enterNavigationMode()
                        updateWidgets()
                    }
                    R.id.itemDelete2AtBottom -> {
                        val buttonClicked = { choice: Boolean ->
                            if (choice) {
                                homeShareViewModel.remove2AllSelectedTodos()
                                enterNavigationMode()
                            }
                        }

                        AlertBottomSheet("Xoá vĩnh viễn các mục đã được chọn?", buttonClicked).show(
                            childFragmentManager,
                            Const.ALERT_BOTTOM_SHEET
                        )
                    }
                    R.id.itemRestoreAtBottom -> {
                        homeShareViewModel.restoreAllSelectedTodos()
                        enterNavigationMode()
                        updateWidgets()
                    }
                    R.id.itemRenameAtBottom -> {
                        val changeGroupName = { newGroupName: String ->
                            homeShareViewModel.changeGroupName(newGroupName)
                            enterNavigationMode()
                        }
                        ChangeGroupNameBottomSheet("", changeGroupName).show(
                            childFragmentManager,
                            Const.CHANGE_GROUP_TITLE
                        )
                    }
                }
                true
            }
        }
    }

    private fun navigationButtonClicked() {
        homeShareViewModel.unSelectAllItem()
        homeShareViewModel.setEditMode(ItemsEditMode.NONE)
        homeShareViewModel.updateOnGoingFragmentData()
        homeShareViewModel.updateGarbageFragmentData()
        enterNavigationMode()
    }

    private val addRemind = { todo: Todo ->
        (activity as MainActivity).addRemind(todo)
    }

    private val removeRemind = { todo: Todo ->
        (activity as MainActivity).removeRemind(todo)
    }

    private fun updateWidgets() {
        (activity as MainActivity).updateWidgets()
    }


    override fun enterEditMode() {
        binding.toolBar.apply {
            menu.clear()
            setNavigationIcon(R.drawable.ic_cancel)
        }
        binding.tabLayout.visibility = View.GONE
        binding.botNav.visibility = View.VISIBLE
        binding.pager.isUserInputEnabled = false
        binding.edtSearchBar.isEnabled = false
        notifyEditModeChange()
    }

    override fun enterNavigationMode() {
        binding.toolBar.apply {
            menu.clear()
            navigationIcon = null
        }
        binding.tabLayout.visibility = View.VISIBLE
        binding.botNav.visibility = View.GONE
        binding.botNav.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_down_anim))
        binding.pager.isUserInputEnabled = true
        binding.edtSearchBar.isEnabled = true
    }

    override fun notifyEditModeChange() {
        when (homeShareViewModel.getEditMode()) {
            ItemsEditMode.ONLY_TODOS -> {
                binding.botNav.apply {
                    menu.clear()
                    inflateMenu(R.menu.home_bottom_toolbar_only_todo_mode)
                }
            }
            ItemsEditMode.ONLY_ONE_GROUP -> {
                binding.botNav.apply {
                    menu.clear()
                    inflateMenu(R.menu.home_bottom_toolbar_only_one_group)
                }
            }
            ItemsEditMode.WITH_GROUP -> {
                binding.botNav.apply {
                    menu.clear()
                    inflateMenu(R.menu.home_bottom_toolbar_with_group_mode)
                }
            }
            ItemsEditMode.ONLY_TODO_IN_GARBAGE -> {
                binding.botNav.apply {
                    menu.clear()
                    inflateMenu(R.menu.home_bottom_toolbar_garbage_mode)
                }
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        binding.botNav.clearAnimation()
        Log.d("HomeFrag", "onDestroyView: ")
        super.onDestroyView()
    }

    override fun onDestroy() {
        //binding.botNav.clearAnimation()
        Log.d("HomeFrag", "onDestroy: ")
        super.onDestroy()
    }
}