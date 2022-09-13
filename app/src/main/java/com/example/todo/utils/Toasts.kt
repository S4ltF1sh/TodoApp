package com.example.todo.utils

import android.content.Context
import android.widget.Toast

object Toasts {
    fun showUnCodedFunctionToast(context: Context?) =
        Toast.makeText(
            context,
            "Chức năng này sẽ được cập nhật trong tương lai!",
            Toast.LENGTH_SHORT
        ).show()

    fun showNewGroupTitleIsAlreadyExistedToast(context: Context?) =
        Toast.makeText(
            context,
            "Nhóm này đã tồn tại!",
            Toast.LENGTH_SHORT
        ).show()

    fun addedNewTodoToast(context: Context?) =
        Toast.makeText(
            context,
            "Đã thêm một việc cần làm!!",
            Toast.LENGTH_SHORT
        ).show()

    fun updatedTodoToast(context: Context?) =
        Toast.makeText(
            context,
            "Đã cập nhật việc cần làm!",
            Toast.LENGTH_SHORT
        ).show()

    fun deletedTodoToast(context: Context?) =
        Toast.makeText(
            context,
            "Đã xoá một việc cần làm!",
            Toast.LENGTH_SHORT
        ).show()

    fun addedNewGroupToast(context: Context?) =
        Toast.makeText(
            context,
            "Đã thêm một nhóm mới!",
            Toast.LENGTH_SHORT
        ).show()

    fun deletedGroupToast(context: Context?, title: String, todosNum: Int) =
        Toast.makeText(
            context,
            "Đã xoá nhóm $title cùng với $todosNum việc cần làm! ",
            Toast.LENGTH_SHORT
        ).show()
}