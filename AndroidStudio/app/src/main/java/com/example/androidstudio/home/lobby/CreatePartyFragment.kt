package com.example.androidstudio.home.lobby

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.types.Lobby
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.UpdateUI
import com.example.androidstudio.home.MenuActivity
import com.google.gson.Gson


class CreatePartyFragment : Fragment(), View.OnClickListener {

    private lateinit var lobbyId: String
    private lateinit var serverHandler: ServerHandler
    private lateinit var lobby: Lobby
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_create_party, container, false)
        serverHandler = ServerHandler(requireContext())

        val menuActivity: MenuActivity = requireActivity() as MenuActivity
        menuActivity.setProfileImageButtonVisibility(View.VISIBLE)
        user = menuActivity.getUser()

        val lobbyString = arguments?.getString("lobby").toString()
        val gson = Gson()
        lobby = gson.fromJson(lobbyString, Lobby::class.java)

        Log.i(Config.LOBBYTAG, lobby.toString())

        lobbyId = arguments?.getString("lobbyId").toString()
        if (lobbyId != "null"){  // room master

        } else { // normal player

        }

        // UID
        val sharedPreferences = requireActivity().getSharedPreferences("lastGoogleId", Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("UID", "").toString()

        // Leave button
        val leaveButton = rootView.findViewById<Button>(R.id.button_leave_lobby)
        leaveButton.setOnClickListener(this)

        // Add friend to lobby button
        val addFriendToLobby = rootView.findViewById<ImageButton>(R.id.lobby_invite_friend_image_button)
        addFriendToLobby.setOnClickListener(this)

        // Android back button
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    leave()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        // Recycler view for team 1 with update every sec
        val inviteFriendListAdapter1 = rootView.findViewById<RecyclerView>(R.id.team1_members_recycler)
        UpdateUI.updateTeamList(requireContext(), this, serverHandler, userid, inviteFriendListAdapter1, true)

        // Recycler view for team 2 with update every sec
        val inviteFriendListAdapter2 = rootView.findViewById<RecyclerView>(R.id.team2_members_recycler)
        UpdateUI.updateTeamList(requireContext(), this, serverHandler, userid, inviteFriendListAdapter2, false)

        return rootView
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_leave_lobby -> leave()
            R.id.lobby_invite_friend_image_button -> invite()
        }

    }

    private fun invite() {
        InviteInPartyFragment(lobby, user).show(requireActivity().supportFragmentManager, "Lobby->Invite")
    }

    private fun leave() {
        serverHandler.apiCall(
            Config.DELETE,
            Config.DELETE_LEAVE_LOBBY,
            lobbyId = lobby.lobbyId,
            userId = user.userId
        )
        AlertDialog.Builder(context)
            .setTitle(R.string.leave_lobby_alert)
            .setMessage(R.string.leave_lobby_alert_message)
            .setPositiveButton(
                R.string.yes
            ) { _, _ ->
                findNavController().popBackStack(R.id.setupGameFragment, false)
            }
            .setNegativeButton(R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}