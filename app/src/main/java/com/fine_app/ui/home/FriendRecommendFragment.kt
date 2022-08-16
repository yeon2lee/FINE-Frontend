package com.fine_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.fine_app.*
import com.fine_app.databinding.FragmentHomeFriendRecommendBinding
import com.fine_app.ui.community.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FriendRecommendFragment : Fragment() {

    private var _binding: FragmentHomeFriendRecommendBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeFriendRecommendBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_recommend_to_navigation_home)
        }
        val viewPager: ViewPager2 = binding.pager
        val tabLayout: TabLayout = binding.tabLayout
        val viewpagerFragmentAdapter = ViewPagerAdapter2(this)

        viewPager.adapter = viewpagerFragmentAdapter

        val tabTitles = listOf("지역", "전공", "취미")

        TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = tabTitles[position] }.attach()

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

