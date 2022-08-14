package com.fine_app.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fine_app.ui.MyPage.MyPageFragment
import com.fine_app.ui.community.CommunityFragment
import com.fine_app.ui.community.CommunityGroupFragment
import com.fine_app.ui.community.CommunityMainFragment
import com.fine_app.ui.friendList.FriendListFragment

class ViewPagerAdapter2 (fragment : FriendRecommendFragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 4
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MatchingAreaFragment()
            1 -> MatchingSchoolFragment()
            2 -> MatchingMajorFragment()
            else -> MatchRandomFragment()
        }
    }
}