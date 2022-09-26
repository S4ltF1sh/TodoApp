package com.example.todo.data.repositories

import com.example.todo.data.models.group.Group
import com.example.todo.data.daos.GroupDao
import com.example.todo.data.models.GroupWithTodos
import java.util.*

class GroupRepository(private val groupDao: GroupDao) {
    fun getAll(): List<Group> = groupDao.getAll()
    fun getGroupByTitle(title: String): Group? = groupDao.getByTitle(title)
    fun getGroupsWithTodos(): List<GroupWithTodos>? = groupDao.getGroupsWithTodos()
    fun getGroupsWithTodosByGroupName(groupName: String): GroupWithTodos =
        groupDao.getGroupWithTodosByGroupName(groupName)

    fun addGroup(newGroup: Group) = groupDao.add(newGroup)
    fun updateGroup(group: Group) = groupDao.update(group)
    fun updateGroupByTitle(oldTitle: String, newTitle: String, newDate: Date?) =
        groupDao.updateByTitle(oldTitle, newTitle, newDate)

    fun removeGroup(group: Group) = groupDao.delete(group)
    fun deleteGroupByTitle(title: String) = groupDao.removeGroupByTitle(title)
}