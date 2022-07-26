package com.fine_app.ui.community

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.fine_app.databinding.FragmentCommunityBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewPager: ViewPager2 = binding.pager
        val tabLayout: TabLayout = binding.tabLayout
        val viewpagerFragmentAdapter = ViewPagerAdapter(this)

        // ViewPager2의 adapter 설정
        viewPager.adapter = viewpagerFragmentAdapter

        // ###### TabLayout 과 ViewPager2를 연결
        // 1. 탭메뉴의 이름을 리스트로 생성해둔다.
        val tabTitles = listOf("일반", "그룹")

        // 2. TabLayout 과 ViewPager2를 연결하고, TabItem 의 메뉴명을 설정한다.
        TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = tabTitles[position] }.attach()

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            //검색 버튼 눌렀을 때 호출
            override fun onQueryTextSubmit(p0: String): Boolean {
                val search= Intent(activity, SearchList::class.java)
                search.putExtra("text", p0)
                startActivity(search)
                binding.searchView.setQuery(null, false)
                return true
            }
            //텍스트 입력과 동시에 호출
            override fun onQueryTextChange(p0: String): Boolean {
                return true
            }
        })
        val writingButton=binding.floatingActionButton
        writingButton.setOnClickListener{
            val writingPage= Intent(activity, Posting::class.java)
            startActivity(writingPage)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

