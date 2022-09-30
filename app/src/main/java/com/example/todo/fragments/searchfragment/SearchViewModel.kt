package com.example.todo.fragments.searchfragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo.data.models.todo.Todo
import com.example.todo.data.repositories.TodoRepository
import java.lang.IllegalArgumentException

class SearchViewModel(private val todoRepository: TodoRepository) : ViewModel() {
    class SearchViewModelFactory(private val todoRepository: TodoRepository) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                return SearchViewModel(todoRepository) as T
            }

            throw IllegalArgumentException("Unable construct viewModel")
        }
    }

    private val searchLiveData = MutableLiveData<List<Todo>?>()
    private val searchData = mutableListOf<Todo>()

    fun getSearchLiveData() = searchLiveData
    fun updateData(keyWord: String) {
        searchData.clear()
        searchData.addAll(getTodosByKeyWord(keyWord))
        searchLiveData.value = searchData
    }

    fun clearData() {
        searchData.clear()
        searchLiveData.value = searchData
    }

    private fun getTodosByKeyWord(keyWord: String) = todoRepository.getByKeyWord(keyWord)

    override fun onCleared() {
        searchData.clear()
        searchLiveData.value = null
        super.onCleared()
    }
}