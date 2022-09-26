package com.example.todo.common

object Const {
    //For Push Notification
    const val CHANNEL_ID = "channel_id"

    //For Reminder:
    const val ACTION_VIEW_TODO_FROM_WIDGET = "remind"
    const val ACTION_ADD_TODO_FROM_WIDGET = "remind name"

    //Bundle Data Key
    const val ITEM_TYPE = "Item Type"
    const val VIEW_TODO_STATUS = "View To Do Status"
    const val ID_TODO_NEED_TO_VIEW = "Todo Need To View"
    const val GROUP_TITLE_NEED_TO_VIEW = "Group Need To View"
    const val TODO_NEED_TO_SHARE = "Todo Need To Share"

    //Databases Name
    const val TODO_DATABASE_NAME = "todo_database"
    const val GROUP_DATABASE_NAME = "group_database"
    const val ACTION_CHECK_DONE_FROM_NOTIFICATION = "Action Check Done From Notification"

    //Dialog Tag
    const val DATE_AND_TIME_PICKER_BOTTOM_SHEET = "Date And Time Picker Bottom Sheet"
    const val GROUP_PICKER_BOTTOM_SHEET = "Group Picker Dialog"
    const val CREATE_NEW_GROUP_BOTTOM_SHEET = "Create New Group Bottom Sheet"
    const val CHANGE_GROUP_TITLE = "Change Group Title"
    const val ALERT_BOTTOM_SHEET = "Alert Bottom Sheet"
}