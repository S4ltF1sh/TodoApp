package com.example.todo.common

enum class ItemsEditMode(value: String) {
    NONE("Not Edit"),
    ONLY_TODOS("Only Work With Todos"),
    WITH_GROUP("Work With Group And Todos"),
    ONLY_ONE_GROUP("Work With Only One Group"),
    ONLY_TODO_IN_GARBAGE("Work With Todos In Garbage"),
    ONLY_TODO_IN_GROUP("Work With Todos In Group")
}