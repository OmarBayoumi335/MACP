package com.example.androidstudio.home.lobby

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.utils.UpdateUI

class PartyInvitationFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_party_invitation, container, false)
        val serverHandler = ServerHandler(requireContext())

        // UID
        val sharedPreferences = requireActivity().getSharedPreferences("lastGoogleId", Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("UID", "").toString()

        // Back button
        val backButton = rootView.findViewById<Button>(R.id.button_back_from_party_invitation)
        backButton.setOnClickListener(this)

        // Recycler view for invites with update every sec
        val lobbyInviteAdapter = rootView.findViewById<RecyclerView>(R.id.party_invites_recycler_view)
        UpdateUI.updateInviteList(requireContext(), this, serverHandler, userid, lobbyInviteAdapter)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_back_from_party_invitation -> back()
        }
    }

    private fun back() {
//        findNavController().navigate(R.id.action_partyInvitationFragment_to_createPartyFragment)
        findNavController().popBackStack()
    }
}