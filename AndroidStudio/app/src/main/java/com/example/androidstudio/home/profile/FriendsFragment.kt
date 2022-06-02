package com.example.androidstudio.home.profile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.classes.adapters.ProfileFriendListAdapter
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendsFragment(profileFragment: ProfileFragment, user: User) : Fragment() {

    private val dataBase = FirebaseDatabase.getInstance().reference

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
        update(profileFriendListAdapter)
        return rootView
    }

    private fun update(profileFriendListAdapter: ProfileFriendListAdapter) {
        dataBase.child("Users").child(user.userId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                profileFriendListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
//        if (this.context != null) {
//            Handler(Looper.getMainLooper()).postDelayed({
//                update(profileFriendListAdapter)
//            },
//                Config.POLLING_PERIOD)
//        }
    }
}