package com.example.androidstudio.classi

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.androidstudio.home.profile.AddFriendFragment
import com.example.androidstudio.home.profile.FriendsFragment
import com.example.androidstudio.home.profile.ProfileFragment
import com.example.androidstudio.home.profile.RequestsFragment

class ProfileViewPageAdapter(profileFragment: ProfileFragment,
                             fm: FragmentManager,
                             lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    private var profileFragment: ProfileFragment

    init {
        this.profileFragment = profileFragment
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FriendsFragment(profileFragment)
            1 -> RequestsFragment(profileFragment)
            2 -> AddFriendFragment()
            else -> FriendsFragment(profileFragment)
        }
    }


}