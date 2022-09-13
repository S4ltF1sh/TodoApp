package com.example.todo.data.daos

import androidx.room.*
import com.example.todo.data.models.GroupWithTodos
import com.example.todo.data.models.group.Group
import java.util.*

@Dao
interface GroupDao {
    @Query("SELECT * FROM group_table")
    fun getAll(): List<Group>

    @Query("SELECT * FROM group_table WHERE title = :title")
    fun getByTitle(title: String): Group?

    @Query("DELETE FROM group_table WHERE title =:title")
    fun removeGroupByTitle(title: String)

    @Query("UPDATE group_table SET title = :newTitle, group_edit_date = :newEditDate WHERE title = :oldTitle")
    fun updateByTitle(oldTitle: String, newTitle: String, newEditDate: Date?)

    @Transaction
    @Query("SELECT * FROM group_table")
    fun getGroupsWithTodos(): List<GroupWithTodos>?

    @Transaction
    @Query("SELECT * FROM group_table WHERE title = :groupName")
    fun getGroupWithTodosByGroupName(groupName: String): GroupWithTodos

    @Insert
    fun add(group: Group)

    @Update
    fun update(group: Group)

    @Delete
    fun delete(group: Group)
}