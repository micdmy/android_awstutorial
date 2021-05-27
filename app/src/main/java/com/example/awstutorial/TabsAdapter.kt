package com.example.awstutorial

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsAdapter(fragment: FragmentActivity) :FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PlayerHomeFragment()
            1 -> MapFragment()
            else -> {
                PlayerHomeFragment()
            }
        }
    }
}