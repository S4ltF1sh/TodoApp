package com.example.todo.fragments.todo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.bottomSheets.CreateNewGroupBottomSheet
import com.example.todo.bottomSheets.DateAndTimePickerBottomSheet
import com.example.todo.bottomSheets.GroupPickerBottomSheet
import com.example.todo.bottomSheets.ShareTodoBottomSheet
import com.example.todo.data.MyDatabase
import com.example.todo.data.models.todo.Todo
import com.example.todo.databinding.FragmentTodoBinding
import com.example.todo.common.Const.CREATE_NEW_GROUP_BOTTOM_SHEET
import com.example.todo.common.Const.DATE_AND_TIME_PICKER_BOTTOM_SHEET
import com.example.todo.common.Const.GROUP_PICKER_BOTTOM_SHEET
import com.example.todo.common.Const.SHARE_TODO_BOTTOM_SHEET
import com.example.todo.common.Const.TODO_NEED_TO_SHARE
import com.example.todo.common.Const.TODO_NEED_TO_VIEW
import com.example.todo.common.Const.VIEW_TODO_STATUS
import com.example.todo.utils.Toasts
import com.example.todo.common.TodoStatus
import com.example.todo.common.ViewTodoStatus
import com.example.todo.data.repositories.GroupRepository
import com.example.todo.data.repositories.TodoRepository
import com.example.todo.utils.ShareUtils
import com.example.todo.utils.SoftKeyBoardUtil
import com.example.todo.utils.SoftKeyBoardUtil.hideKeyboard
import com.example.todo.utils.TimeUtil
import java.util.*

class TodoFragment : Fragment() {
    private lateinit var binding: FragmentTodoBinding
    private lateinit var todoRepository: TodoRepository
    private lateinit var groupRepository: GroupRepository
    private val todoViewModel: TodoViewModel by lazy {
        ViewModelProvider(
            this,
            TodoViewModel.TodoNeedToViewViewModelFactory(
                todoRepository, groupRepository
            )
        )[TodoViewModel::class.java]
    }

    private var todoViewMode = ViewTodoStatus.VIEW_MODE
    private var currentTodoStatus = TodoStatus.ON_GOING
    private var currentAlarmDate: Date? = null
    private var currentGroupName: String = ""

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
        binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val todoDao = MyDatabase.getInstance(requireContext()).todoDao
        val groupDao = MyDatabase.getInstance(requireContext()).groupDao
        todoRepository = TodoRepository(todoDao)
        groupRepository = GroupRepository(groupDao)

        setContent()
        setVisibility()
        setOnClick()
        setOnTouch()
        setOnTextChange()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setContent() {
        todoViewMode = arguments?.get(VIEW_TODO_STATUS) as ViewTodoStatus
        if (todoViewMode == ViewTodoStatus.VIEW_MODE) {
            val argument = arguments?.get(TODO_NEED_TO_VIEW) as Todo
            currentAlarmDate = argument.alarmDate
            currentGroupName = argument.groupName
            currentTodoStatus = argument.todoStatus
            todoViewModel.setData(argument)
            todoViewModel.getTodoLiveData().observe(viewLifecycleOwner) {
                binding.apply {
                    chipTime.text = TimeUtil.format(currentAlarmDate)
                    chipGroup.text =
                        if (currentGroupName != "") currentGroupName else "Chưa phân loại"
                    chipTime.text = TimeUtil.format(it?.alarmDate)
                    edtTitle.setText(todoViewModel.getTitle())
                    edtNote.setText(todoViewModel.getNote())
                }
            }
        }
    }

    private fun setVisibility() {
        if (currentTodoStatus == TodoStatus.ON_GOING)
            when (todoViewMode) {
                ViewTodoStatus.VIEW_MODE -> enterViewMode()
                ViewTodoStatus.ADD_MODE -> {
                    binding.edtTitle.requestFocus()
                    SoftKeyBoardUtil.showSoftKeyboard(requireActivity())
                }
                else -> {}
            }
        else {
            enterViewDeleteMenu()
            binding.edtTitle.isEnabled = false
            binding.edtNote.isEnabled = false
            binding.chipGroup.isEnabled = false
            binding.chipTime.isEnabled = false
        }
        binding.chipTime.isCloseIconVisible = currentAlarmDate != null
    }

    private fun setOnClick() {
        binding.apply {
            toolBar.setNavigationOnClickListener { backButtonClicked() }
            toolBar.setOnMenuItemClickListener(onMenuItemClickListener())
            chipTime.setOnClickListener { pickTime() }
            chipTime.setOnCloseIconClickListener { setNewTime(null) }
            chipGroup.setOnClickListener { pickGroup() }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouch() {
        binding.apply {
            edtTitle.setOnTouchListener { _, _ ->
                if (todoViewMode == ViewTodoStatus.VIEW_MODE)
                    enterEditMode()
                else
                    enterEditMenu()
                false
            }

            edtNote.setOnTouchListener { _, _ ->
                if (todoViewMode == ViewTodoStatus.VIEW_MODE)
                    enterEditMode()
                else
                    enterEditMenu()
                false
            }
        }
    }

    private fun setOnTextChange() {
        binding.apply {
            edtTitle.doAfterTextChanged {
                afterTextChange()
            }

            edtNote.doAfterTextChanged {
                afterTextChange()
            }
        }
    }

    private fun afterTextChange() {
        binding.apply {
            if (todoViewMode != ViewTodoStatus.VIEW_MODE) {
                if (isEmptyTodo()) {
                    toolBar.menu.clear()
                } else if (todoViewMode == ViewTodoStatus.EDIT_MODE) {
                    enterEditMode()
                } else if (todoViewMode == ViewTodoStatus.ADD_MODE) {
                    enterEditMenu()
                }
            }
        }
    }

    private fun isEmptyTodo(): Boolean {
        val currentTitle = binding.edtTitle.text.toString().trim()
        val currentNote = binding.edtNote.text.toString().trim()
        return currentTitle == "" && currentNote == ""
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun onMenuItemClickListener() = { item: MenuItem ->
        when (item.itemId) {
            R.id.itemSave -> saveButtonClicked()
            R.id.itemShare -> shareButtonClicked()
            R.id.itemMoveTo -> pickGroup()
            R.id.itemAddToMainScreen -> {
                Toasts.showUnCodedFunctionToast(context)
                //TODO("Chưa cập nhật chức năng tạo widget trên màn hình chính!")
            }
            R.id.itemDelete -> delete1ButtonClicked()
            R.id.itemDelete2 -> delete2ButtonClicked()
            R.id.itemRestore -> restoreButtonClicked()
        }

        false
    }

    private fun saveButtonClicked() {
        when (todoViewMode) {
            ViewTodoStatus.VIEW_MODE -> {}
            ViewTodoStatus.EDIT_MODE -> {
                updateCurrentTodo()
                enterViewMode()
            }
            ViewTodoStatus.ADD_MODE -> {
                enterViewMenu()
            }
        }
        binding.edtTitle.clearFocus()
        binding.edtNote.clearFocus()
        hideKeyboard()
    }

    private fun backButtonClicked() {
        when (todoViewMode) {
            ViewTodoStatus.ADD_MODE -> {
                if (!isEmptyTodo())
                    addNewTodo()
            }
            else -> {
                if (isEmptyTodo())
                    deleteTodo()
                else {
                    updateCurrentTodo()
                    todoViewModel.updateTodo()
                }
            }
        }

        binding.toolBar.findNavController().popBackStack()
    }

    private fun shareButtonClicked() {
        val currentTitle: String = binding.edtTitle.text.toString().trim()
        val currentNote: String = binding.edtNote.text.toString().trim()
        val byteArray = ShareUtils.getTodoByteArray(binding.scrollView)
        val argument = Bundle()
        argument.putByteArray(TODO_NEED_TO_SHARE, byteArray)

        val shareInImageFormat = {
            findNavController().navigate(
                R.id.action_viewTodoFragment_to_shareTodoInImageFormatFragment,
                argument
            )
        }
        ShareTodoBottomSheet(shareInImageFormat).show(childFragmentManager, SHARE_TODO_BOTTOM_SHEET)
    }

    private fun delete1ButtonClicked() {
        if (currentTodoStatus == TodoStatus.ON_GOING) {
            currentTodoStatus = TodoStatus.DELETED
            updateCurrentTodo()
        }
        Toasts.deletedTodoToast(context)
        findNavController().popBackStack()
    }

    private fun delete2ButtonClicked() = deleteTodo()

    private fun restoreButtonClicked() {
        currentTodoStatus = TodoStatus.ON_GOING
        enterViewMode()
        todoViewModel.setGroupName(todoViewModel.getGroupName())
        binding.edtTitle.isEnabled = true
        binding.edtNote.isEnabled = true
        binding.chipGroup.isEnabled = true
        binding.chipTime.isEnabled = true
    }

    //Time And Group
    private fun pickTime() {
        DateAndTimePickerBottomSheet(setNewTime).show(
            childFragmentManager,
            DATE_AND_TIME_PICKER_BOTTOM_SHEET
        )
    }

    private val setNewTime = { newDate: Date? ->
        currentAlarmDate = newDate
        binding.chipTime.isCloseIconVisible = currentAlarmDate != null
        binding.chipTime.text = TimeUtil.format(newDate)
    }

    private fun pickGroup() {
        val allGroups = MyDatabase.getInstance(requireContext()).groupDao.getAll()

        GroupPickerBottomSheet(allGroups, openBottomSheetToCreateNewGroup, setNewGroup).show(
            childFragmentManager,
            GROUP_PICKER_BOTTOM_SHEET
        )
    }

    private val openBottomSheetToCreateNewGroup = {
        CreateNewGroupBottomSheet(currentGroupName, setNewGroup).show(
            childFragmentManager,
            CREATE_NEW_GROUP_BOTTOM_SHEET
        )
    }

    private val setNewGroup = { newGroupName: String ->
        currentGroupName = newGroupName
        binding.chipGroup.text = if (newGroupName != "") newGroupName else "Chưa phân loại"
        todoViewModel.addNewGroup(newGroupName)
    }

    //add, remove, update
    private fun updateCurrentTodo() {
        val currentTitle: String = binding.edtTitle.text.toString().trim()
        val currentNote: String = binding.edtNote.text.toString().trim()
        val editDate = TimeUtil.currentTime()

        todoViewModel.setTodoStatus(currentTodoStatus)
        todoViewModel.setGroupName(currentGroupName)
        todoViewModel.setTime(currentAlarmDate, editDate)
        todoViewModel.setTitle(currentTitle)
        todoViewModel.setNote(currentNote)
        todoViewModel.updateTodo()
    }

    private fun addNewTodo() {
        val currentTitle: String = binding.edtTitle.text.toString().trim()
        val currentNote: String = binding.edtNote.text.toString().trim()
        val editDate = TimeUtil.currentTime()
        val newTodo =
            Todo(
                0,
                currentTitle,
                currentNote,
                alarmDate = currentAlarmDate,
                currentGroupName,
                editDate = editDate,
            )
        todoViewModel.setData(newTodo)
        todoViewModel.addNewTodo(newTodo)

        Toasts.addedNewTodoToast(context)
    }

    private fun deleteTodo() {
        todoViewModel.delete2Todo()
        Toasts.deletedTodoToast(context)
        findNavController().popBackStack()
    }

    //view modes
    private fun enterViewMode() {
        enterViewMenu()
        Log.d("Mode: ", "View")
        clearFocus()
        todoViewMode = ViewTodoStatus.VIEW_MODE
    }

    private fun enterEditMode() {
        enterEditMenu()
        Log.d("Mode: ", "Edit")
        todoViewMode = ViewTodoStatus.EDIT_MODE
    }

    private fun enterEditMenu() {
        binding.toolBar.apply {
            menu.clear()
            inflateMenu(R.menu.view_todo_edit_mode)
        }
    }

    private fun enterViewMenu() {
        binding.toolBar.apply {
            menu.clear()
            inflateMenu(R.menu.view_todo_view_mode)
        }
    }

    private fun enterViewDeleteMenu() {
        binding.toolBar.apply {
            menu.clear()
            inflateMenu(R.menu.view_deleted_todo)
        }
    }

    private fun clearFocus() {
        binding.edtTitle.clearFocus()
        binding.edtNote.clearFocus()
    }
}