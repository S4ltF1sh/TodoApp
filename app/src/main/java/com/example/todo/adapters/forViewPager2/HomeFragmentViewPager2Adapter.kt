package com.example.todo.adapters.forViewPager2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.todo.fragments.home.GarbageFragment
import com.example.todo.fragments.home.OnGoingFragment

class HomeFragmentViewPager2Adapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val onGoingBotNavListener: OnGoingFragment.OnGoingBotNavListener,
    private val garbageBotNavListener: GarbageFragment.GarbageBotNavListener
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnGoingFragment(onGoingBotNavListener)
            else -> GarbageFragment(garbageBotNavListener)
        }
    }
}