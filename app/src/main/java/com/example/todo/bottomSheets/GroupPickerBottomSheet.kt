package com.example.todo.bottomSheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todo.adapters.forRV.GroupPickerRVAdapter
import com.example.todo.data.models.group.Group
import com.example.todo.databinding.BottomSheetGroupPickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GroupPickerBottomSheet(
    private val groups: List<Group>,
    private val openBottomSheetToCreateNewGroup: () -> Unit,
    private val setNewGroup: (String) -> Unit
) :
    BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetGroupPickerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetGroupPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setContent()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setContent() {
        val adapter = GroupPickerRVAdapter(createNewGroup, newGroupSelected)
        binding.rvGroupPicker.adapter = adapter
        adapter.setItems(groups)
    }

    private val newGroupSelected = { newGroupName: String ->
        setNewGroup(newGroupName)
        this.dismiss()
    }

    private val createNewGroup = {
        openBottomSheetToCreateNewGroup()
        this.dismiss()
    }
}