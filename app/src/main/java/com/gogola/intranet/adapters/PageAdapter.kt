package com.gogola.intranet.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.gogola.intranet.fragments.*

class PageAdapter(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int = 5

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> PostsFragment()
            1 -> OwnPostsFragment()
            2 -> ProfileFragment()
            3 -> UserSearchFragment()
            else -> FriendsFragment()
        }
}
