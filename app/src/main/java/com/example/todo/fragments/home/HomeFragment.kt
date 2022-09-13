package com.example.todo.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.todo.R
import com.example.todo.adapters.forViewPager2.HomeFragmentViewPager2Adapter
import com.example.todo.common.ItemsEditMode
import com.example.todo.data.MyDatabase
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
            HomeShareViewModel.HomeShareViewModelFactory(todoRepository, groupRepository)
        )[HomeShareViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val todoDao = MyDatabase.getInstance(requireContext()).todoDao
        val groupDao = MyDatabase.getInstance(requireContext()).groupDao
        todoRepository = TodoRepository(todoDao)
        groupRepository = GroupRepository(groupDao)

        setupTabLayout()
        setupSearchView()
        setupBotNav()
        setOnClick()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupBotNav() {
        binding.botNav.itemActiveIndicatorColor = null
        binding.botNav.itemRippleColor = null
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
        binding.searchView.apply {
            queryHint = "Tìm kiếm việc cần làm"

            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.toolBar.visibility = View.GONE
                    binding.pager.isUserInputEnabled = false
                } else {
                    binding.toolBar.visibility = View.VISIBLE
                    binding.pager.isUserInputEnabled = true
                }
            }

            setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {

                override fun onQueryTextChange(newText: String): Boolean {

                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

            })

            setOnCloseListener {
                homeShareViewModel.updateOnGoingFragmentData()
                binding.tabLayout.visibility = View.VISIBLE
                true
            }
        }
    }

    private fun setOnClick() {
        binding.apply {
            toolBar.setNavigationOnClickListener { navigationButtonClicked() }
            botNav.setOnItemSelectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.itemDeleteAtBottom -> {
                        homeShareViewModel.remove1AllSelectedItems()
                        enterNavigationMode()
                    }
                    R.id.itemMoveTo -> {
                        //GroupPickerBottomSheet()
                    }
                    R.id.itemCheckDoneAtBottom -> {
                        homeShareViewModel.checkDoneAllSelectedTodos()
                        enterNavigationMode()
                    }
                    R.id.itemDelete2AtBottom -> {

                    }
                    R.id.itemRestoreAtBottom -> {
                        homeShareViewModel.restoreAllSelectedTodos()
                        enterNavigationMode()
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

    override fun enterEditMode() {
        binding.toolBar.apply {
            menu.clear()
            setNavigationIcon(R.drawable.ic_cancel)
        }
        binding.tabLayout.visibility = View.GONE
        binding.botNav.visibility = View.VISIBLE
        binding.pager.isUserInputEnabled = false
        notifyEditModeChange()
    }

    override fun enterNavigationMode() {
        binding.toolBar.apply {
            menu.clear()
            navigationIcon = null
        }
        binding.tabLayout.visibility = View.VISIBLE
        binding.botNav.visibility = View.GONE
        binding.pager.isUserInputEnabled = true
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
}