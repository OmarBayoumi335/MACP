package com.example.androidstudio.home.lobby

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.adapters.LobbyTeamAdapter
import com.example.androidstudio.classes.adapters.ProfileFriendListAdapter
import com.example.androidstudio.classes.types.Lobby
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.UpdateUI
import com.example.androidstudio.home.MenuActivity
import com.google.gson.Gson
import org.json.JSONObject


class CreatePartyFragment : Fragment(), View.OnClickListener {

    private lateinit var serverHandler: ServerHandler
    private lateinit var lobby: Lobby
    private lateinit var user: User
    private lateinit var team1NumEditText: TextView
    private lateinit var team2NumEditText: TextView
    private lateinit var team1MembersAdapter: LobbyTeamAdapter
    private lateinit var team2MembersAdapter: LobbyTeamAdapter

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

        // Title
        val lobbyNameTextView = rootView.findViewById<TextView>(R.id.lobby_title_name_textview)
        lobbyNameTextView.text = lobby.lobbyName

        // Num members
        team1NumEditText = rootView.findViewById(R.id.team1_number_of_members_textview)
        team2NumEditText = rootView.findViewById(R.id.team2_number_of_members_textview)
        team1NumEditText.text = lobby.team1.size.toString().plus("/"+Config.MAX_LOBBY_MEMBERS)
        team2NumEditText.text = lobby.team2.size.toString().plus("/"+Config.MAX_LOBBY_MEMBERS)

        // Change Team Button
        val changeTeamImageButton = rootView.findViewById<ImageButton>(R.id.change_team_image_button)
        changeTeamImageButton.setOnClickListener(this)

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
        val team1MembersRecyclerView = rootView.findViewById<RecyclerView>(R.id.team1_members_recycler)
        team1MembersAdapter = LobbyTeamAdapter(lobby, true)
        team1MembersRecyclerView.adapter = team1MembersAdapter
        team1MembersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Recycler view for team 2 with update every sec
        val team2MembersRecyclerView = rootView.findViewById<RecyclerView>(R.id.team2_members_recycler)
        team2MembersAdapter = LobbyTeamAdapter(lobby, false)
        team2MembersRecyclerView.adapter = team2MembersAdapter
        team2MembersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // update con get lobby
        return rootView
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_leave_lobby -> leave()
            R.id.lobby_invite_friend_image_button -> invite()
            R.id.change_team_image_button -> changeTeam()
        }

    }

    private fun changeTeam() {
        var teamNumber = 2
        var teamIndex = 0
        for (i in 0 until lobby.team1.size) {
            if (lobby.team1[i].userId == user.userId) {
                teamNumber = 1
                teamIndex = i
                break
            }
        }
        if (teamNumber == 2) {
            for (i in 0 until lobby.team1.size) {
                if (lobby.team1[i].userId == user.userId) {
                    teamIndex = i
                    break
                }
            }
        }
        if (teamNumber == 1) {
            lobby.team2.add(lobby.team1[teamIndex])
            lobby.team1.remove(lobby.team1[teamIndex])
        } else {
            lobby.team1.add(lobby.team2[teamIndex])
            lobby.team2.remove(lobby.team2[teamIndex])
        }
        // update views
        team1NumEditText.text = lobby.team1.size.toString().plus("/"+Config.MAX_LOBBY_MEMBERS)
        team2NumEditText.text = lobby.team2.size.toString().plus("/"+Config.MAX_LOBBY_MEMBERS)
        team1MembersAdapter.notifyDataSetChanged()
        team2MembersAdapter.notifyDataSetChanged()
        serverHandler.apiCall(
            Config.POST,
            Config.POST_CHANGE_TEAM,
            userId = user.userId,
            lobbyId = lobby.lobbyId
        )
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

//    fun updateLobby(createPartyFragment: CreatePartyFragment,
//                    serverHandler: ServerHandler,
//                    user: User,
//                    notification: TextView) {
//        Log.i(Config.UPDATEUITAG, "updateUser() $user")
//        serverHandler.apiCall(
//            Config.GET,
//            Config.GET_USER,
//            userId = user.userId,
//            callBack = object : ServerHandler.VolleyCallBack {
//                override fun onSuccess(reply: JSONObject?) {
//                    val userJsonString = reply.toString()
//                    val gson = Gson()
//                    val userUpdate = gson.fromJson(userJsonString, User::class.java)
//                    user.username = userUpdate.username
//                    user.friends = userUpdate.friends
//                    user.pendingFriendRequests = userUpdate.pendingFriendRequests
//                    user.pendingInviteRequests = userUpdate.pendingInviteRequests
////                        user.roomMaster = userUpdate.roomMaster
//                    if (user.pendingFriendRequests != null) {
//                        notification.visibility = View.VISIBLE
//                        notification.text = user.pendingFriendRequests?.size.toString()
//                    } else {
//                        notification.visibility = View.GONE
//                    }
//                    if (profileFragment.context != null) {
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            UpdateUI.updateUser(
//                                menuActivity,
//                                serverHandler,
//                                user,
//                                notification
//                            )
//                        }, Config.POLLING_PERIOD)
//                    }
//                }
//            })
//    }
}