package com.example.todo.bottomSheets

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.example.todo.R
import com.example.todo.data.MyDatabase
import com.example.todo.databinding.BottomSheetCreateNewGroupBinding
import com.example.todo.utils.SoftKeyBoardUtil
import com.example.todo.utils.Toasts
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_create_new_group.*

class ChangeGroupNameBottomSheet(
    private val oldTitle: String,
    private val changeGroupTitle: (String) -> Unit,
) :
    BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetCreateNewGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FABBottomSheet)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCreateNewGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setContent()
        setVisibility()
        setOnClick()
        setOnTextChange()
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    private fun setContent() {
        binding.tvDialogTitle.text = "Đổi tên nhóm"
        binding.edtNewGroupName.setText(oldTitle)
        binding.edtNewGroupName.requestFocus()
        SoftKeyBoardUtil.showSoftKeyboard(requireActivity())
    }

    private fun setVisibility() {
        if (oldTitle == "")
            binding.btnOK.isEnabled = false
    }

    private fun setOnClick() {
        binding.apply {
            btnCancel.setOnClickListener { buttonCancelClicked() }
            btnOK.setOnClickListener { buttonOKClicked() }
        }
    }

    private fun buttonCancelClicked() = this.dismiss()


    private fun buttonOKClicked() {
        val newGroupTitle = binding.edtNewGroupName.text.toString().trim()
        val aGroup = MyDatabase.getInstance(requireContext()).groupDao.getByTitle(newGroupTitle)
        if (aGroup == null) {
            changeGroupTitle(newGroupTitle)
            Toasts.addedNewGroupToast(context)
            this.dismiss()
        } else {
            Toasts.showNewGroupTitleIsAlreadyExistedToast(context)
            binding.btnOK.isEnabled = false
        }
    }

    private fun setOnTextChange() {
        binding.edtNewGroupName.doAfterTextChanged {
            binding.btnOK.isEnabled = edtNewGroupName.text.toString().trim().let {
                it.isNotEmpty() && it.length <= 100
            }
        }
    }
}