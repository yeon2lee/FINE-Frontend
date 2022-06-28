package com.fine_app.ui.Community

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.Locale.filter

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