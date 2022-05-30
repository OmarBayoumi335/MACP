package com.example.androidstudio.home.profile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.adapters.ProfileFriendListAdapter
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config

class FriendsFragment(profileFragment: ProfileFragment, user: User) : Fragment() {

    private var profileFragment: ProfileFragment
    private var user: User

    init {
        this.profileFragment = profileFragment
        this.user = user
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_friends, container, false)
        val serverHandler = ServerHandler(requireContext())

        // Recycler view with update every sec
        val profileFriendsRecyclerView = rootView.findViewById<RecyclerView>(R.id.profile_friends_list_recyclerView)
        val profileFriendListAdapter = ProfileFriendListAdapter(
            requireContext(),
            user,
            0,
            serverHandler
        )
        profileFriendsRecyclerView.adapter = profileFriendListAdapter
        profileFriendsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        update(serverHandler, profileFriendListAdapter)
        return rootView
    }

    private fun update(serverHandler: ServerHandler,
                       profileFriendListAdapter: ProfileFriendListAdapter) {
        profileFriendListAdapter.notifyDataSetChanged()
        if (this.context != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                update(serverHandler, profileFriendListAdapter)
            },
                Config.POLLING_PERIOD)
        }
    }
}