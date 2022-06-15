package com.example.androidstudio.home.lobby

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.home.MenuActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SetupGameFragment : Fragment(), View.OnTouchListener {

    private val dataBase = FirebaseDatabase.getInstance().reference

    private lateinit var user: User
    private lateinit var rootView: View

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_setup_game, container, false)

        // user
        val menuActivity: MenuActivity = requireActivity() as MenuActivity
        user = menuActivity.getUser()

        // Create party
        val createPartyButton = rootView.findViewById<Button>(R.id.party_creation_button)
        createPartyButton.setOnTouchListener(this)

        // back button
        val backButton = rootView.findViewById<Button>(R.id.button_back_from_setup_game)
        backButton.setOnTouchListener(this)

        // party invitation button
        val partyInvitationButton = rootView.findViewById<Button>(R.id.party_invitations_button)
        partyInvitationButton.setOnTouchListener(this)

        // invites notification
        val invitationsNotification = rootView.findViewById<TextView>(R.id.invite_notification_textView)
        if (user.pendingInviteRequests == null || user.pendingInviteRequests?.size == 0) {
            invitationsNotification.visibility = View.GONE
        } else {
            invitationsNotification.visibility = View.VISIBLE
            invitationsNotification.text = user.pendingInviteRequests!!.size.toString()
        }

        update(invitationsNotification)
        return rootView
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, motionEvent: MotionEvent?): Boolean {
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        when (motionEvent?.action) {
            MotionEvent.ACTION_DOWN -> v?.startAnimation(scaleDown)
            MotionEvent.ACTION_UP -> {
                v?.startAnimation(scaleUp)
                when (v?.id) {
                    R.id.button_back_from_setup_game -> back()
                    R.id.party_creation_button -> createParty()
                    R.id.party_invitations_button -> openInvitations()
                }
            }
        }
        return true
    }

    private fun openInvitations() {
        findNavController().navigate(R.id.action_setupGameFragment_to_partyInvitationFragment)
    }

    private fun createParty() {
        val loadingFragment = SelectNameLobbyDialogFragment(user)
        loadingFragment.show(parentFragmentManager, "Setup->Loading")
    }

    private fun back() {
        findNavController().popBackStack()
    }

    private fun update(invitationsNotification: TextView) {
        dataBase.child("Users").child(user.userId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (user.pendingInviteRequests == null || user.pendingInviteRequests?.size == 0) {
                    invitationsNotification.visibility = View.GONE
                } else {
                    invitationsNotification.visibility = View.VISIBLE
                    invitationsNotification.text = user.pendingInviteRequests!!.size.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val invitationsNotification = rootView.findViewById<TextView>(R.id.invite_notification_textView)
        if (user.pendingInviteRequests == null || user.pendingInviteRequests?.size == 0) {
            invitationsNotification.visibility = View.GONE
        } else {
            invitationsNotification.visibility = View.VISIBLE
            invitationsNotification.text = user.pendingInviteRequests!!.size.toString()
        }
    }
}