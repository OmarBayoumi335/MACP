package com.example.androidstudio.classes.adapters

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.home.profile.AddFriendFragment
import com.example.androidstudio.home.profile.FriendsFragment
import com.example.androidstudio.home.profile.ProfileFragment
import com.example.androidstudio.home.profile.RequestsFragment

class ProfileViewPageAdapter(profileFragment: ProfileFragment,
                             fm: FragmentManager,
                             lifecycle: Lifecycle,
                             user: User,
                             notificationInProfile: TextView,
                             notificationProfile: TextView) : FragmentStateAdapter(fm, lifecycle) {

    private var profileFragment: ProfileFragment
    private var user: User
    private var notificationInProfile: TextView
    private var notificationProfile: TextView

    init {
        this.profileFragment = profileFragment
        this.user = user
        this.notificationProfile = notificationProfile
        this.notificationInProfile = notificationInProfile
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FriendsFragment(profileFragment, user)
            1 -> RequestsFragment(
                profileFragment,
                user,
                notificationInProfile,
                notificationProfile
            )
            2 -> AddFriendFragment(user)
            else -> FriendsFragment(profileFragment, user)
        }
    }
}