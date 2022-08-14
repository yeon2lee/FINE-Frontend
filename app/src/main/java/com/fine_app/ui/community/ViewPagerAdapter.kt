package com.fine_app.ui.community

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fine_app.ui.home.FriendRecommendFragment

class ViewPagerAdapter (fragment : CommunityFragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                CommunityMainFragment()
            }
            else -> CommunityGroupFragment()
        }
    }
}