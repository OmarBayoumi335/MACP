package com.example.androidstudio.home.lobby

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.androidstudio.R
class SetupGameFragment : Fragment(), View.OnClickListener {

    private var numberInvites: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setup_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backButton = view.findViewById<Button>(R.id.button_back_from_setup_game)
        backButton.setOnClickListener(this)
        val createPartyButton = view.findViewById<Button>(R.id.party_creation_button)
        createPartyButton.setOnClickListener(this)
        val partyInvitationButton = view.findViewById<Button>(R.id.party_invitations_button)
        partyInvitationButton.setOnClickListener(this)

        // numberInvites = api

        val invitationsNotification = view.findViewById<TextView>(R.id.invite_notification_textView)
        if (numberInvites == 0) {
            invitationsNotification.visibility = View.GONE
        } else {
            invitationsNotification.text = numberInvites.toString()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_back_from_setup_game -> back()
            R.id.party_creation_button -> createParty()
            R.id.party_invitations_button -> openInvitations()
        }
    }

    private fun openInvitations() {
        numberInvites = 0
        //api = numberInvites
        findNavController().navigate(R.id.action_setupGameFragment_to_partyInvitationFragment)
    }

    private fun createParty() {
        findNavController().navigate(R.id.action_setupGameFragment_to_createPartyFragment)
    }

    private fun back() {
        findNavController().popBackStack()
    }
}