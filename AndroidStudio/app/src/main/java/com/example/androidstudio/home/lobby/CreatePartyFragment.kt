package com.example.androidstudio.home.lobby

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidstudio.R


class CreatePartyFragment : Fragment(), View.OnClickListener {

    private lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_create_party, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val leaveButton = view.findViewById<Button>(R.id.button_leave_lobby)
        leaveButton.setOnClickListener(this)
        val addFriendToLobby = view.findViewById<ImageButton>(R.id.lobby_invite_friend_image_button)
        addFriendToLobby.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_leave_lobby -> leave()
            R.id.lobby_invite_friend_image_button -> invite()
        }

    }

    private fun invite() {
        val inflater = LayoutInflater.from(context)
        val viewAlert = inflater.inflate(R.layout.invite_friend_alert, null)
        val alertDialog: AlertDialog = AlertDialog.Builder(context).setView(viewAlert).create()
        val closeButton = viewAlert.findViewById<Button>(R.id.invite_friend_close_button)
        closeButton.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun leave() {
        AlertDialog.Builder(context)
            .setTitle(R.string.leave_lobby_alert)
            .setMessage(R.string.leave_lobby_alert_message)
            .setPositiveButton(
                R.string.yes
            ) { _, _ ->
                findNavController().popBackStack()
            }
            .setNegativeButton(R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}