package com.fine_app.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter2 (fragment : FriendRecommendFragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MatchingAreaFragment()
            1 -> MatchingMajorFragment()
            else -> MatchingHobbyFragment()
        }
    }
}