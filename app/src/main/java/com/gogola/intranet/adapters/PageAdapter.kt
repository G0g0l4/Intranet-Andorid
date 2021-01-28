package com.gogola.intranet.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.gogola.intranet.fragments.FriendsFragment
import com.gogola.intranet.fragments.PostsFragment
import com.gogola.intranet.fragments.UserSearchFragment

class PageAdapter(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> PostsFragment()
            1 -> UserSearchFragment()
            else -> FriendsFragment()
        }
}
