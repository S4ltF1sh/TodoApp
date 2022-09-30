package com.example.todo.fragments.searchfragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.adapters.forRV.TodoRVAdapter
import com.example.todo.common.Const
import com.example.todo.common.ItemsEditMode
import com.example.todo.common.ViewTodoStatus
import com.example.todo.data.MyDatabase
import com.example.todo.data.models.Item
import com.example.todo.data.models.todo.Todo
import com.example.todo.data.repositories.TodoRepository
import com.example.todo.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var todoRepository: TodoRepository
    private val searchViewModel: SearchViewModel by lazy {
        ViewModelProvider(
            this,
            SearchViewModel.SearchViewModelFactory(todoRepository)
        )[SearchViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val todoDao = MyDatabase.getInstance(requireContext()).todoDao
        todoRepository = TodoRepository(todoDao)

        val adapter = TodoRVAdapter(viewTodo, restoreTodo, getEditMode, selectItem, unSelectItem)
        binding.rvSearchedTodos.adapter = adapter
        binding.edtSearchBar.requestFocus()

        searchViewModel.getSearchLiveData().observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.setData(it)
                if (it.isNotEmpty())
                    binding.tvShrugFace.visibility = View.GONE
                else
                    binding.tvShrugFace.visibility = View.VISIBLE
            }
        }

        binding.edtSearchBar.doAfterTextChanged {
            Handler(Looper.getMainLooper()).postDelayed({
                afterTextChange()
            }, 500)
        }
        super.onViewCreated(view, savedInstanceState)
    }


    private val viewTodo = { todo: Todo, extras: FragmentNavigator.Extras ->
        val argument = Bundle()
        argument.putInt(Const.ID_TODO_NEED_TO_VIEW, todo.id)
        argument.putSerializable(Const.VIEW_TODO_STATUS, ViewTodoStatus.VIEW_MODE)

        findNavController().navigate(
            R.id.action_searchFragment_to_viewTodoFragment,
            argument,
            null,
            extras
        )
    }

    private fun afterTextChange() {
        val keyWord = binding.edtSearchBar.text.toString().trim()
        if (keyWord != "")
            searchViewModel.updateData(keyWord)
        else
            searchViewModel.clearData()
    }

    private val restoreTodo = { _: Todo, _: Int -> }

    private val selectItem = { _: Item -> }

    private val unSelectItem = { _: Item -> }

    private val getEditMode = { ItemsEditMode.NONE }
}