package com.example.todo.fragments.todo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.bottomSheets.AlertBottomSheet
import com.example.todo.bottomSheets.CreateNewGroupBottomSheet
import com.example.todo.bottomSheets.DateAndTimePickerBottomSheet
import com.example.todo.bottomSheets.GroupPickerBottomSheet
import com.example.todo.common.Const
import com.example.todo.data.MyDatabase
import com.example.todo.data.models.todo.Todo
import com.example.todo.databinding.FragmentTodoBinding
import com.example.todo.common.Const.CREATE_NEW_GROUP_BOTTOM_SHEET
import com.example.todo.common.Const.DATE_AND_TIME_PICKER_BOTTOM_SHEET
import com.example.todo.common.Const.GROUP_PICKER_BOTTOM_SHEET
import com.example.todo.common.Const.ID_TODO_NEED_TO_VIEW
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
import com.google.android.material.transition.MaterialContainerTransform
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
        Log.i("TodoFrag", "onAttach")
        super.onAttach(context)
        val callback: OnBackPressedCallback = object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backButtonClicked()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("TodoFrag", "onCreate")
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
        binding = FragmentTodoBinding.inflate(inflater, container, false)
        Log.i("TodoFrag", "onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i("TodoFrag", "onViewCreated")
        val todoDao = MyDatabase.getInstance(requireContext()).todoDao
        val groupDao = MyDatabase.getInstance(requireContext()).groupDao
        todoRepository = TodoRepository(todoDao)
        groupRepository = GroupRepository(groupDao)

        setContent()
        setVisibility()
        setOnClick()
        setOnFocusChange()
        setOnTextChange()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setContent() {
        todoViewMode = arguments?.get(VIEW_TODO_STATUS) as ViewTodoStatus
        if (todoViewMode == ViewTodoStatus.VIEW_MODE) {
            val id = arguments?.getInt(ID_TODO_NEED_TO_VIEW)
            val argument = todoRepository.getById(id!!)
            Log.i("TodoFrag", "todo: $argument")
            currentAlarmDate = argument.alarmDate
            currentGroupName = argument.groupName
            currentTodoStatus = argument.todoStatus
            todoViewModel.setData(argument)
            todoViewModel.getTodoLiveData().observe(viewLifecycleOwner) {
                binding.apply {
                    chipTime.text = TimeUtil.format(currentAlarmDate)
                    if (currentAlarmDate != null && currentAlarmDate!! < TimeUtil.currentTime())
                        chipTime.setTextColor(Color.parseColor("#ef476f"))
                    else
                        chipTime.setTextColor(Color.parseColor("#FFFFFF"))
                    chipGroup.text =
                        if (currentGroupName != "") currentGroupName else "Chưa phân loại"
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
            chipTime.setOnClickListener {
                pickTime()
                enterEditMenu()
            }
            chipTime.setOnCloseIconClickListener {
                setNewTime(null)
                updateCurrentTodo()
            }
            chipGroup.setOnClickListener {
                pickGroup()
                enterEditMenu()
            }
        }
    }

    private fun setOnFocusChange() {
        binding.apply {
            edtTitle.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (todoViewMode == ViewTodoStatus.VIEW_MODE)
                        enterEditMode()
                    else
                        enterEditMenu()
                }
            }

            edtNote.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (todoViewMode == ViewTodoStatus.VIEW_MODE)
                        enterEditMode()
                    else
                        enterEditMenu()
                }
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
            R.id.itemMoveTo -> {
                pickGroup()
                enterEditMenu()
            }
            R.id.itemDelete -> {
                delete1ButtonClicked()
                findNavController().popBackStack()
            }
            R.id.itemDelete2 -> {
                val buttonClicked = { choice: Boolean ->
                    if (choice) {
                        delete2ButtonClicked()
                        findNavController().popBackStack()
                    }
                }

                AlertBottomSheet("Xoá vĩnh viễn việc cần làm?", buttonClicked).show(
                    childFragmentManager,
                    Const.ALERT_BOTTOM_SHEET
                )

            }
            R.id.itemRestore -> restoreButtonClicked()
        }

        false
    }

    private fun saveButtonClicked() {
        when (todoViewMode) {
            ViewTodoStatus.ADD_MODE -> {
                if (!isEmptyTodo())
                    addNewTodo()
                enterViewMode()
            }
            else -> {
                if (!isEmptyTodo())
                    updateCurrentTodo()
                enterViewMode()
            }
        }
        binding.edtTitle.clearFocus()
        binding.edtNote.clearFocus()
        hideKeyboard()
    }

    private fun backButtonClicked() {
        binding.toolBar.findNavController().popBackStack()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun shareButtonClicked() {
        val currentTitle: String = binding.edtTitle.text.toString().trim()
        val currentNote: String = binding.edtNote.text.toString().trim()
        val intent = ShareUtils().genNewSendTextIntent(currentTitle, currentNote)
        startActivity(intent)
    }

    private fun delete1ButtonClicked() {
        if (currentTodoStatus == TodoStatus.ON_GOING) {
            currentTodoStatus = TodoStatus.DELETED
            updateCurrentTodo()
        }
        Toasts.deletedTodoToast(context)
    }

    private fun delete2ButtonClicked() {
        todoViewModel.getTodoLiveData().value?.let { (activity as MainActivity).removeRemind(it) }
        todoViewModel.delete2Todo()
    }

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

        if (currentTodoStatus == TodoStatus.ON_GOING)
            updateRemind()
        updateWidgets()
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
        addRemind(todoViewModel.addNewTodo(newTodo))
        updateWidgets()
        Toasts.addedNewTodoToast(context)
    }

    private fun addRemind(newTodo: Todo) {
        if (newTodo.alarmDate != null) {
            (activity as MainActivity).addRemind(newTodo)
        }
    }

    private fun updateRemind() {
        val currentTodo = todoViewModel.getTodoLiveData().value
        if (currentTodo != null) {
            (activity as MainActivity).removeRemind(currentTodo)
            if (currentAlarmDate != null)
                (activity as MainActivity).addRemind(currentTodo)
        }
    }

    private fun updateWidgets() {
        (activity as MainActivity).updateWidgets()
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

    override fun onDestroyView() {
        Log.i("TodoFrag", "onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.i("TodoFrag", "onDestroy")
        super.onDestroy()
    }
}