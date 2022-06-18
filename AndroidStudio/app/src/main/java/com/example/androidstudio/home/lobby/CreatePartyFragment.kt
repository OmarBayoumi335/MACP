package com.example.androidstudio.home.lobby

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.adapters.ChatAdapter
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.classes.adapters.LobbyTeamAdapter
import com.example.androidstudio.classes.types.Lobby
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.game.GameActivity
import com.example.androidstudio.home.MenuActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.json.JSONObject


class CreatePartyFragment : Fragment(), View.OnTouchListener {

    private val dataBase = FirebaseDatabase.getInstance().reference

    private lateinit var serverHandler: ServerHandler
    private lateinit var lobby: Lobby
    private lateinit var user: User
    private lateinit var menuActivity: MenuActivity
    private lateinit var team1NumEditText: TextView
    private lateinit var team2NumEditText: TextView
    private lateinit var team1MembersAdapter: LobbyTeamAdapter
    private lateinit var team2MembersAdapter: LobbyTeamAdapter
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var changeTeamImageButton: ImageButton
    private lateinit var chatImageButton: ImageButton
    private lateinit var chatEditText: EditText
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var readyButton: Button
    private lateinit var leaveButton: Button
    private lateinit var addFriendToLobby: ImageButton
    private var gameCanStart: Boolean = true
    private var canSend: Boolean = true
    private lateinit var res: Resources

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_create_party, container, false)
        serverHandler = ServerHandler(requireContext())
        res  = resources

        val lobbyString = arguments?.getString("lobby").toString()
        val gson = Gson()
        lobby = gson.fromJson(lobbyString, Lobby::class.java)

        menuActivity = requireActivity() as MenuActivity
        menuActivity.setProfileImageButtonVisibility(View.VISIBLE)
        menuActivity.setLobby(lobby)
        user = menuActivity.getUser()

        Log.i(Config.LOBBY_TAG, lobby.toString())

        // Title
        val lobbyNameTextView = rootView.findViewById<TextView>(R.id.lobby_title_name_textview)
        lobbyNameTextView.text = lobby.lobbyName

        // Num members
        team1NumEditText = rootView.findViewById(R.id.team1_number_of_members_textview)
        team2NumEditText = rootView.findViewById(R.id.team2_number_of_members_textview)
        team1NumEditText.text = lobby.team1.size.toString().plus("/"+Config.MAX_TEAM_MEMBERS)
        team2NumEditText.text = lobby.team2.size.toString().plus("/"+Config.MAX_TEAM_MEMBERS)

        // Change Team Button
        changeTeamImageButton = rootView.findViewById<ImageButton>(R.id.change_team_image_button)
        changeTeamImageButton.setOnTouchListener(this)
        changeTeamImageButton.isFocusableInTouchMode = true

        // Leave button
        leaveButton = rootView.findViewById<Button>(R.id.button_leave_lobby)
        leaveButton.setOnTouchListener(this)
        leaveButton.isFocusableInTouchMode = true

        // Ready button
        readyButton = rootView.findViewById(R.id.lobby_ready_button)
        readyButton.setOnTouchListener(this)
        readyButton.isFocusableInTouchMode = true

        // Add friend to lobby button
        addFriendToLobby = rootView.findViewById(R.id.lobby_invite_friend_image_button)
        addFriendToLobby.setOnTouchListener(this)
        addFriendToLobby.isFocusableInTouchMode = true

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

        // Chat
        chatRecyclerView = rootView.findViewById(R.id.chat_lobby)
        chatAdapter = ChatAdapter(requireContext(), lobby, user)
        chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount-1)
        chatAdapter.notifyDataSetChanged()
        chatRecyclerView.adapter = chatAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
            reverseLayout = false
        }
        chatImageButton = rootView.findViewById(R.id.lobby_chat_send_button)
        chatImageButton.setOnTouchListener(this)
        chatImageButton.isFocusableInTouchMode = true
        chatEditText = rootView.findViewById(R.id.lobby_chat_edit_text)

        // update con get lobby
        updateLobby(requireActivity())
        return rootView
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, motionEvent: MotionEvent?): Boolean {
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        if (v?.isFocusableInTouchMode!!) {
            when (motionEvent?.action) {
                MotionEvent.ACTION_DOWN -> v.startAnimation(scaleDown)
                MotionEvent.ACTION_UP -> {
                    v.startAnimation(scaleUp)
                    v.isFocusableInTouchMode = false
                    when (v.id) {
                        R.id.button_leave_lobby -> leave()
                        R.id.change_team_image_button -> changeTeam()
                        R.id.lobby_ready_button -> ready()
                        R.id.lobby_invite_friend_image_button -> invite()
                        R.id.lobby_chat_send_button -> sendMessage()
                    }
                }
            }
        }
        return true
    }

    private fun leave() {
        leaveButton.isFocusableInTouchMode = true
        AlertDialog.Builder(context, R.style.BackgroundDialog)
            .setTitle(R.string.leave_lobby_alert)
            .setMessage(R.string.leave_lobby_alert_message)
            .setPositiveButton(
                R.string.yes
            ) { _, _ ->
                serverHandler.apiCall(
                    Config.DELETE,
                    Config.DELETE_LEAVE_LOBBY,
                    lobbyId = lobby.lobbyId,
                    userId = user.userId
                )
                findNavController().popBackStack(R.id.setupGameFragment, false)
            }
            .setNegativeButton(R.string.no, null)
            .setIcon(R.drawable.alert)
            .show()
            .findViewById<TextView>(android.R.id.message).typeface =
            Typeface.createFromAsset(requireContext().assets, "booster_next_fy_black.ttf")
    }

    private fun invite() {
        addFriendToLobby.isFocusableInTouchMode = true
        InviteInPartyFragment(lobby, user).show(
            requireActivity().supportFragmentManager,
            "Lobby->Invite"
        )
    }

    private fun changeTeam() {
        gameCanStart = false
        serverHandler.apiCall(
            Config.POST,
            Config.POST_CHANGE_TEAM,
            userId = user.userId,
            lobbyId = lobby.lobbyId,
            callBack = object : ServerHandler.VolleyCallBack{
                override fun onSuccess(reply: JSONObject?) {
                    changeTeamImageButton.isFocusableInTouchMode = true
                    val status = reply?.get("status")
                    if (status == "notFullTeam") {
                        val start: Boolean = reply.getBoolean("start")
                        if (start && !gameCanStart) {
                            val intent = intentToGame(true)
                            lobby.start = start
                            menuActivity.setLobby(lobby)
                            startActivity(intent)
                            requireActivity().overridePendingTransition(0, 0);
                            requireActivity().finish()
                        } else {
                            gameCanStart = true
                        }
                    } else {
                        gameCanStart = true
                    }
                }
            }
        )
    }

    private fun sendMessage() {
        var textToSend = chatEditText.text.toString()
        var allSpace = true
        for (i in textToSend.indices) {
            if (textToSend[i] != ' ') {
                textToSend = textToSend.substring(i)
                allSpace = false
                break
            }
        }
        if (!allSpace) {
            if (canSend) {
                canSend = false
                serverHandler.apiCall(
                    Config.POST,
                    Config.POST_SEND_MESSAGE,
                    userId = user.userId,
                    username = user.username,
                    lobbyId = lobby.lobbyId,
                    chatText = textToSend,
                    callBack = object : ServerHandler.VolleyCallBack {
                        override fun onSuccess(reply: JSONObject?) {
                            chatImageButton.isFocusableInTouchMode = true
                            canSend = true
                            chatEditText.text.clear()
                        }
                    },
                    callBackError = object : ServerHandler.VolleyCallBackError {
                        override fun onError() {
                            chatImageButton.isFocusableInTouchMode = true
                            canSend = true
                        }
                    }
                )
            }
        } else {
            chatImageButton.isFocusableInTouchMode = true
        }
        Log.i(Config.LOBBY_TAG, "message: ->$textToSend<-")
    }

    private fun ready() {
        if (readyButton.text == resources.getString(R.string.lobby_cancel)) {
            readyButton.text = resources.getString(R.string.lobby_ready)
        } else {
            readyButton.text = resources.getString(R.string.lobby_cancel)
        }
        gameCanStart = false
        serverHandler.apiCall(
            Config.POST,
            Config.POST_CHANGE_READY_STATUS,
            userId = user.userId,
            lobbyId = lobby.lobbyId,
            callBack = object : ServerHandler.VolleyCallBack {
                override fun onSuccess(reply: JSONObject?) {
                    readyButton.isFocusableInTouchMode = true
                    val start: Boolean? = reply?.getBoolean("start")
                    if (start!! && !gameCanStart) {
                        val intent = intentToGame(true)
                        lobby.start = start
                        menuActivity.setLobby(lobby)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(0, 0);
                        requireActivity().finish()
                    } else {
                        gameCanStart = true
                    }
                }
            })
    }

    private fun intentToGame(starter: Boolean): Intent? {
        val intent = Intent(context, GameActivity::class.java)
        intent.putExtra("starter", starter)
        intent.putExtra("gameLobbyId", generateGameLobbyId())
        intent.putExtra("userId", user.userId)
        intent.putExtra("lobbyId", lobby.lobbyId)
        intent.putExtra("lobbyGameMembers", lobby.team1.size + lobby.team2.size)
        intent.putExtra("captainIndex1", lobby.team1.random().userId)
        intent.putExtra("captainIndex2", lobby.team2.random().userId)
        intent.putExtra("team", getTeam())
        val gson = Gson()
        val lobbyStringJSON = gson.toJson(lobby)
        intent.putExtra("lobby", lobbyStringJSON)
        return intent
    }

    private fun getTeam(): String? {
        for (member in lobby.team1) {
            if (member.userId == user.userId) {
                return resources.getString(R.string.team1)
            }
        }
        return resources.getString(R.string.team2)
    }

    private fun generateGameLobbyId(): String? {
        var gameLobbyId = ""
        for (member in lobby.team1) {
            gameLobbyId = gameLobbyId.plus("-" + member.userId)
        }
        for (member in lobby.team2) {
            gameLobbyId = gameLobbyId.plus("-" + member.userId)
        }
        return gameLobbyId.substring(1)
    }

    private fun updateUI(newLobby: Lobby) {
        lobby.team1 = newLobby.team1
        lobby.team2 = newLobby.team2
        if (lobby.chat != newLobby.chat) {
            lobby.chat = newLobby.chat
            chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
            chatAdapter.notifyDataSetChanged()
        }
        for (member in lobby.team1 + lobby.team2) {
            if (member.userId == user.userId) {
                readyButton.text = if (member.ready) {
                    res.getString(R.string.lobby_cancel)
                } else {
                    res.getString(R.string.lobby_ready)
                }
                break
            }
        }
        team1NumEditText.text = lobby.team1.size.toString().plus("/"+Config.MAX_TEAM_MEMBERS)
        team2NumEditText.text = lobby.team2.size.toString().plus("/"+Config.MAX_TEAM_MEMBERS)
        team1MembersAdapter.notifyDataSetChanged()
        team2MembersAdapter.notifyDataSetChanged()
    }

    private fun updateLobby(requireActivity: FragmentActivity) {
        dataBase.child("Lobbies").child(lobby.lobbyId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val newLobby = snapshot.getValue(Lobby::class.java)
                if (newLobby != null) {
                    if (newLobby.start && gameCanStart) {
                        gameCanStart = false
                        val intent = intentToGame(false)
                        menuActivity.setLobby(newLobby)
                        startActivity(intent)
                        requireActivity.overridePendingTransition(0, 0);
                        requireActivity.finish()
                    }
                    updateUI(newLobby)
                    Log.i(Config.LOBBY_TAG, "updateLobby() $lobby")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}