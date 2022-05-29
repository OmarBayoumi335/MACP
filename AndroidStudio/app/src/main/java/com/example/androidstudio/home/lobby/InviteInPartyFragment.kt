package com.example.androidstudio.home.lobby

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.utils.UpdateUI

class InviteInPartyFragment(lobbyId: String) : DialogFragment(), View.OnClickListener {

    private var lobbyId: String

    init {
        this.lobbyId = lobbyId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_invite_in_party, container, false)

        // UID
        val sharedPreferences = requireActivity().getSharedPreferences("lastGoogleId", Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("UID", "").toString()

        // Close button
        val closeButton = rootView.findViewById<Button>(R.id.invite_friend_close_button)
        closeButton.setOnClickListener (this)

        // Recycler view with update every sec
        val inviteFriendListRecyclerView = rootView.findViewById<RecyclerView>(R.id.invite_friend_recyclerview)
        val serverHandler = ServerHandler(requireContext())
        UpdateUI.updateInviteFriendsList(requireContext(), this, serverHandler, userid, inviteFriendListRecyclerView, lobbyId)

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
}