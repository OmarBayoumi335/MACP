package com.example.androidstudio.home.lobby

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.adapters.InviteFriendListAdapter
import com.example.androidstudio.classes.adapters.ProfileFriendListAdapter
import com.example.androidstudio.classes.types.Lobby
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.UpdateUI

class InviteInPartyFragment(lobby: Lobby, user: User) : DialogFragment(), View.OnClickListener {

    private var lobby: Lobby
    private var user: User

    init {
        this.lobby = lobby
        this.user = user
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_invite_in_party, container, false)

        // Close button
        val closeButton = rootView.findViewById<Button>(R.id.invite_friend_close_button)
        closeButton.setOnClickListener (this)

        // Recycler view with update every sec
        val inviteFriendListRecyclerView = rootView.findViewById<RecyclerView>(R.id.invite_friend_recyclerview)
        val serverHandler = ServerHandler(requireContext())
        val inviteFriendListAdapter = InviteFriendListAdapter(user, lobby, serverHandler, requireContext())
        inviteFriendListRecyclerView.adapter = inviteFriendListAdapter
        inviteFriendListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        update(inviteFriendListAdapter)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the fragment dimension to 90% of the device size
        val w = (resources.displayMetrics.widthPixels * 0.80).toInt()
        val h = (resources.displayMetrics.heightPixels * 0.80).toInt()
        val viewResize: View = view.findViewById(R.id.invite_in_party_fragment)
        val layoutParams: ViewGroup.LayoutParams? = viewResize.layoutParams
        layoutParams?.width = w
        layoutParams?.height = h
        viewResize.layoutParams = layoutParams

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.invite_friend_close_button -> dismiss()
        }
    }

    private fun update(inviteFriendListAdapter: InviteFriendListAdapter) {
        inviteFriendListAdapter.notifyDataSetChanged()
        if (this.context != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                update(inviteFriendListAdapter)
            },
                Config.POLLING_PERIOD)
        }
    }
}