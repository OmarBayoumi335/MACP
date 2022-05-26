package com.example.androidstudio.home.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classi.Config
import com.example.androidstudio.classi.ServerHandler
import com.example.androidstudio.classi.UpdateUI
import kotlinx.android.synthetic.main.fragment_profile.*

class FriendsFragment(profileFragment: ProfileFragment) : Fragment() {

    private var profileFragment: ProfileFragment

    init {
        this.profileFragment = profileFragment
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_friends, container, false)

        // UID
        val sharedPreferences = requireActivity().getSharedPreferences("lastGoogleId",Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("UID", "").toString()

        // Recycler view with update every sec
        val profileFriendsRecyclerView = rootView.findViewById<RecyclerView>(R.id.profile_friends_list_recyclerView)
        val serverHandler = ServerHandler(requireContext())
        UpdateUI.updateFriendsList(requireContext(), profileFragment, serverHandler, userid, profileFriendsRecyclerView)

        return rootView
    }
}