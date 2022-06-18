package com.example.androidstudio.game

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.adapters.ChatAdapter
import com.example.androidstudio.classes.adapters.ChatGameAdapter
import com.example.androidstudio.classes.types.*
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.game.views.GameView
import com.example.androidstudio.game.views.GuessCardView
import com.example.androidstudio.home.MenuActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.json.JSONObject

class GameFragment : Fragment(), View.OnTouchListener{

    private val dataBase = FirebaseDatabase.getInstance().reference
    private lateinit var serverHandler: ServerHandler
    private lateinit var gameLobby: GameLobby
    private lateinit var userGame: UserGame
    private lateinit var gameView: GameView
    private lateinit var buttonDirection: TextView
    private lateinit var buttonNumberHint: Button
    private lateinit var buttonPass: Button
    private lateinit var selectNumberHintLayout: ConstraintLayout
    private lateinit var buttonValue1: Button
    private lateinit var buttonValue2: Button
    private lateinit var buttonValue3: Button
    private lateinit var buttonValue4: Button
    private lateinit var buttonValue5: Button
    private lateinit var buttonValue6: Button
    private lateinit var giveClue: Button
    private lateinit var gameWordHint: EditText
    private lateinit var gameActivity: GameActivity
    private var selectNumber = false
    private var selectText = false
    private var ended = true
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatGameAdapter
    private lateinit var chatImageButton: ImageButton
    private lateinit var chatEditText: EditText
    private var canSend: Boolean = true

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        gameView = GameView(requireContext())
        gameView.activity = requireActivity()
        serverHandler = ServerHandler(requireContext())
        val gson = Gson()

        // Game lobby info
        val gameLobbyString = arguments?.getString("gameLobby")
        gameLobby = gson.fromJson(gameLobbyString, GameLobby::class.java)

        // User in game info
        val userGameString = arguments?.getString("userGame")
        userGame = gson.fromJson(userGameString, UserGame::class.java)

//        gameLobby = GameLobby(lobbyId="79emietECeNnUaGuPw88fHjdCvKDeBJbpcJmad3DLH",
//            members=mutableListOf(UserGame(userId="mad3DLH",
//                username="5",
//                team="Team Green",
//                vote=100,
//                ready=false),
//                UserGame(userId="HjdCvKD",
//                    username="4",
//                    team="Team Green",
//                    vote=100,
//                    ready=false),
//                UserGame(userId="79emiet",
//                    username="senza numero",
//                    team="Team Green",
//                    vote=100,
//                    ready=false),
//                UserGame(userId="ECeNnUa",
//                    username="polloooo",
//                    team="Team Red",
//                    vote=100,
//                    ready=false),
//                UserGame(userId="eBJbpcJ",
//                    username="gugu",
//                    team="Team Red",
//                    vote=100,
//                    ready=false),
//                UserGame(userId="GuPw88f",
//                    username="dADdd",
//                    team="Team Red",
//                    vote=100,
//                    ready=false)),
//            chatTeam1=mutableListOf(),
//            chatTeam2=mutableListOf(),
//            turn="Team Red",
//            turnPhase=1,
//            words=mutableListOf(
//                Word("importer", "green", "SOUTH", false),
//                Word("esok", "gray", "NORTH", true),
//                Word("morbidity", "green", "EAST", false),
//                Word("kitsch", "red", "WEST", false),
//                Word("tune", "green", "SOUTH", true),
//                Word("haddock", "red", "SOUTH", false),
//                Word("zookeeper", "green", "WEST", false),
//                Word("ecologist", "gray", "NORTH", false),
//                Word("fruitbat", "green", "SOUTH", false),
//                Word("hen", "red", "NORTH", true),
//                Word("zigzag salamander", "red", "EAST", false),
//                Word("apatosaur", "gray", "SOUTH", false),
//                Word("kerosene", "green", "SOUTH", false),
//                Word("glowworm", "red", "NORTH", false),
//                Word("factotum", "red", "SOUTH", false),
//                Word("city", "black", "SOUTH", true)
//            ),
//            captainIndex1="mad3DLH1",
//            captainIndex2="mad3DLH1",
//            hint1=3,
//            hint2=3,
//            clue=Clue("ciao", 3, mutableListOf("SOUTH")),
//            winner="no")

//        userGame = UserGame(userId="mad3DLH", username="5", team="Team Red", vote=100, ready=false)

        Log.i(Config.GAME_TAG, "\ngameLobby: $gameLobby\nuserGame: $userGame")

        gameActivity = requireActivity() as GameActivity
        gameActivity.setGameLobby(gameLobby)
        gameActivity.setUserGame(userGame)

        // Disable Android back button
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    leave()
                }
            }
        gameActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // pass lobby and user in the view
        gameView.gameLobby = gameLobby
        gameView.userGame = userGame

        // button to choose direction for the clue
        buttonDirection = gameActivity.findViewById(R.id.game_direction_hint)
        buttonDirection.setOnTouchListener(this)

        // buttons to choose the number for the clue
        selectNumberHintLayout = gameActivity.findViewById(R.id.game_select_number_hint)
        buttonNumberHint = gameActivity.findViewById(R.id.game_number_hint)
        buttonNumberHint.setOnTouchListener(this)
        // hint number 1
        buttonValue1 = gameActivity.findViewById(R.id.game_button_value_1)
        buttonValue1.setOnTouchListener(this)
        // hint number 2
        buttonValue2 = gameActivity.findViewById(R.id.game_button_value_2)
        buttonValue2.setOnTouchListener(this)
        // hint number 3
        buttonValue3 = gameActivity.findViewById(R.id.game_button_value_3)
        buttonValue3.setOnTouchListener(this)
        // hint number 4
        buttonValue4 = gameActivity.findViewById(R.id.game_button_value_4)
        buttonValue4.setOnTouchListener(this)
        // hint number 5
        buttonValue5 = gameActivity.findViewById(R.id.game_button_value_5)
        buttonValue5.setOnTouchListener(this)
        // hint number 6
        buttonValue6 = gameActivity.findViewById(R.id.game_button_value_6)
        buttonValue6.setOnTouchListener(this)

        // edit text to choose the word for the clue
        gameWordHint = gameActivity.findViewById(R.id.game_word_hint)
        gameWordHint.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                selectText = s.toString() != ""
                checkClue()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        // button that give the clue for the members
        giveClue = gameActivity.findViewById(R.id.game_confirm_hint)
        giveClue.setOnTouchListener(this)
        giveClue.isFocusableInTouchMode = true

        // button for pass the game phase without voting
        buttonPass = gameActivity.findViewById(R.id.game_pass_hint_member)
        buttonPass.setOnTouchListener(this)

        // Chat
        chatRecyclerView = gameActivity.findViewById(R.id.game_chat_recyclerview)
        chatAdapter = ChatGameAdapter(gameLobby, userGame, requireContext())
        chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount-1)
        chatAdapter.notifyDataSetChanged()
        chatRecyclerView.adapter = chatAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
            reverseLayout = false
        }
        chatEditText = gameActivity.findViewById(R.id.game_message_edittext)
        chatImageButton = gameActivity.findViewById(R.id.game_send_message_image_button)
        if (gameLobby.captainIndex1 == userGame.userId || gameLobby.captainIndex2 == userGame.userId) {
            chatEditText.visibility = View.GONE
            chatImageButton.visibility = View.GONE
        }
        chatImageButton.setOnTouchListener(this)

        // title, send button of the team chat box
        val chatBoxTitle = gameActivity.findViewById<TextView>(R.id.game_chat_team_title_textview)
        if (userGame.team == resources.getString(R.string.team1)) {
            chatImageButton.setImageResource(R.drawable.send_message_game1)
            chatBoxTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.chat_game_team1_title))
        } else {
            chatImageButton.setImageResource(R.drawable.send_message_game2)
            chatBoxTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.chat_game_team2_title))
        }

        // set bottom part view
        updateBottomPart()

        // set right part view
        updateRightPart()

        // update game status
        updateGameLobby()
        return gameView
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        if (!v?.isFocusableInTouchMode!!) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> v.startAnimation(scaleDown)
                MotionEvent.ACTION_UP -> {
                    v.startAnimation(scaleUp)
                    when (v.id) {
                        R.id.game_direction_hint -> showDirectionHintFragment()
                        R.id.game_number_hint -> showNumbersHint()
                        R.id.game_button_value_1 -> selectNumberHint("1")
                        R.id.game_button_value_2 -> selectNumberHint("2")
                        R.id.game_button_value_3 -> selectNumberHint("3")
                        R.id.game_button_value_4 -> selectNumberHint("4")
                        R.id.game_button_value_5 -> selectNumberHint("5")
                        R.id.game_button_value_6 -> selectNumberHint("6")
                        R.id.game_confirm_hint -> giveClueToMembers()
                        R.id.game_pass_hint_member -> passButton()
                        R.id.game_send_message_image_button -> sendMessage()
                    }
                }
            }
        }
        return true
    }

    private fun leave(notEnoughPlayer: Boolean = false, teamNotEnoughPlayer: String = "") {
        if (notEnoughPlayer) {
            ended = false
            val message = if (teamNotEnoughPlayer == userGame.team) {
                resources.getString(R.string.not_enough_player_my_team_alert_message)
            } else {
                resources.getString(R.string.not_enough_player_opponent_team_alert_message)
            }
            AlertDialog.Builder(context, R.style.BackgroundDialog)
                .setTitle(R.string.not_enough_player)
                .setMessage(message)
                .setPositiveButton(
                    R.string.come_back_home
                ) { _, _ ->
                    serverHandler.apiCall(
                        Config.GET,
                        Config.GET_USER,
                        userId = userGame.userId,
                        callBack = object : ServerHandler.VolleyCallBack {
                            override fun onSuccess(reply: JSONObject?) {
                                val userJsonString = reply.toString()
                                val intent = Intent(requireContext(), MenuActivity::class.java)
                                intent.putExtra("user", userJsonString)
                                startActivity(intent)
                                requireActivity().overridePendingTransition(0, 0);
                                requireActivity().finish()
//                                serverHandler.apiCall(
//                                    Config.DELETE,
//                                    Config.DELETE_LEAVE_GAMELOBBY,
//                                    userId = userGame.userId,
//                                    gameLobbyId = gameLobby.lobbyId
//                                )
                            }
                        })
                }
                .setIcon(R.drawable.alert)
                .setCancelable(false)
                .show()
                .findViewById<TextView>(android.R.id.message).typeface =
                Typeface.createFromAsset(requireContext().assets, "booster_next_fy_black.ttf")
        } else {
            AlertDialog.Builder(context, R.style.BackgroundDialog)
                .setTitle(R.string.leave_game_lobby_alert)
                .setMessage(R.string.leave_game_lobby_alert_message)
                .setPositiveButton(
                    R.string.yes
                ) { _, _ ->
                    ended = false
                    serverHandler.apiCall(
                        Config.GET,
                        Config.GET_USER,
                        userId = userGame.userId,
                        callBack = object : ServerHandler.VolleyCallBack {
                            override fun onSuccess(reply: JSONObject?) {
                                val userJsonString = reply.toString()
                                val intent = Intent(requireContext(), MenuActivity::class.java)
                                intent.putExtra("user", userJsonString)
                                startActivity(intent)
                                requireActivity().overridePendingTransition(0, 0);
                                requireActivity().finish()
//                                serverHandler.apiCall(
//                                    Config.DELETE,
//                                    Config.DELETE_LEAVE_GAMELOBBY,
//                                    userId = userGame.userId,
//                                    gameLobbyId = gameLobby.lobbyId
//                                )
                            }
                        })
                }
                .setNegativeButton(R.string.no, null)
                .setIcon(R.drawable.alert)
                .show()
                .findViewById<TextView>(android.R.id.message).typeface =
                Typeface.createFromAsset(requireContext().assets, "booster_next_fy_black.ttf")
        }
    }

    private fun selectNumberHint(value: String){
        selectNumberHintLayout.visibility = View.GONE
        buttonNumberHint.text = value
        selectNumber = true
        checkClue()
    }

    private fun showNumbersHint(){
        if (selectNumberHintLayout.visibility == View.VISIBLE) {
            selectNumberHintLayout.visibility = View.GONE
        } else {
            selectNumberHintLayout.visibility = View.VISIBLE
        }
    }

    private fun showDirectionHintFragment(){
        val chooseDirectionsFragment = if (userGame.team == resources.getString(R.string.team1)) {
            ChooseDirectionFragment(gameLobby.hint1, userGame.team)
        } else {
            ChooseDirectionFragment(gameLobby.hint2, userGame.team)
        }
        chooseDirectionsFragment.show(gameActivity.supportFragmentManager, "GameFragment->ChooseDirectionFragment")
    }

    private fun giveClueToMembers() {
        val text = gameWordHint.text.toString()
        val number = buttonNumberHint.text.toString().toInt()
        var directions = mutableListOf<String>()
        if (buttonDirection.text.toString() != "-") {
            directions = buttonDirection.text.toString().split(" ").toMutableList()
        }
        val clue = Clue(text, number, directions)
        serverHandler.apiCall(
            Config.POST,
            Config.POST_SEND_CLUE,
            userId = userGame.userId,
            gameLobbyId = gameLobby.lobbyId,
            clue = clue.toString()
        )
    }

    private fun passButton() {
        if (userGame.vote != 16) {
            buttonPass.setTextColor(ContextCompat.getColor(requireContext(), R.color.button_inside_card_text))
            buttonPass.text = resources.getString(R.string.don_t_pass_button)
            buttonPass.isFocusableInTouchMode = true
            serverHandler.apiCall(
                Config.POST,
                Config.POST_VOTE,
                userId = userGame.userId,
                gameLobbyId = gameLobby.lobbyId,
                clue = gameLobby.clue.toString(),
                team = userGame.team,
                voteIndex = "16"
            )
        }
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
                buttonPass.isFocusableInTouchMode = true
                serverHandler.apiCall(
                    Config.POST,
                    Config.POST_SEND_MESSAGE_GAMELOBBY,
                    userId = userGame.userId,
                    gameLobbyId = gameLobby.lobbyId,
                    username = userGame.username,
                    chatText = textToSend,
                    callBack = object : ServerHandler.VolleyCallBack {
                        override fun onSuccess(reply: JSONObject?) {
                            buttonPass.isFocusableInTouchMode = false
                            canSend = true
                            chatEditText.text.clear()
                        }
                    }
                )
            }
        }
        Log.i(Config.GAME_TAG, "message: ->$textToSend<-")
        chatEditText.text.clear()
    }

    private fun checkClue() {
        giveClue.isFocusableInTouchMode = !selectNumber || !selectText
    }

    private fun updateBottomPart() {
        Log.i(Config.GAME_VIEW_TAG, gameLobby.toString())
        if (gameLobby.turn == userGame.team) { // my turn
            if ((userGame.userId != gameLobby.captainIndex1 && userGame.userId != gameLobby.captainIndex2)) { // not captain
                if (gameLobby.turnPhase == 0) {
                    gameActivity.setViewOpponentTurn(gameLobby.clue, gameLobby.turnPhase, gameLobby.turn)
                } else {
                    gameActivity.setViewMyTurnMember(gameLobby.clue, gameLobby.turnPhase, gameLobby.turn)
                }
            } else { // captain
                if (gameLobby.turnPhase == 1) {
                    gameActivity.setViewOpponentTurn(gameLobby.clue, gameLobby.turnPhase, gameLobby.turn)
                } else {
                    gameActivity.setViewMyTurnCaptain()
                }
            }
        } else { // opponent turn
            gameActivity.setViewOpponentTurn(gameLobby.clue, gameLobby.turnPhase, gameLobby.turn)
        }
        if (userGame.vote != 16) {
            buttonPass.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            buttonPass.text = resources.getString(R.string.pass_button)
            buttonPass.isFocusableInTouchMode = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateRightPart() {
        // turn title
        val turn = gameActivity.findViewById<TextView>(R.id.game_turn_team_textview)
        val phase = if (gameLobby.turnPhase == 0) {
            resources.getString(R.string.phase1)
        } else {
            resources.getString(R.string.phase2)
        }
        if (gameLobby.turn == resources.getString(R.string.team1)) {
            turn.setTextColor(ContextCompat.getColor(requireContext(), R.color.chat_game_team1_title))
        } else {
            turn.setTextColor(ContextCompat.getColor(requireContext(), R.color.chat_game_team2_title))
        }
        turn.text = "${gameLobby.turn}: $phase"

        // chat title
        val chatTitle = gameActivity.findViewById<TextView>(R.id.game_chat_team_title_textview)
        chatTitle.text = userGame.team
    }

    private fun updateGameLobbyUI(newGameLobby: GameLobby) {
        if (ended) {
            var team1Members = 0
            var team2Members = 0
            for (member in newGameLobby.members) {
                if (member.team == resources.getString(R.string.team1)) {
                    team1Members += 1
                } else {
                    team2Members += 1
                }
            }
            if (team1Members < 2) {
                leave(true, resources.getString(R.string.team1))
            }
            if (team2Members < 2) {
                leave(true, resources.getString(R.string.team2))
            }
        }
        if (gameLobby != newGameLobby && ended) {
            gameLobby.lobbyId = newGameLobby.lobbyId
            gameLobby.members = newGameLobby.members
            if (userGame.team == resources.getString(R.string.team1)) {
                if (gameLobby.chatTeam1 != newGameLobby.chatTeam1) {
                    gameLobby.chatTeam1 = newGameLobby.chatTeam1
                    chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount-1)
                    chatAdapter.notifyDataSetChanged()

                }
            } else {
                if (gameLobby.chatTeam2 != newGameLobby.chatTeam2) {
                    gameLobby.chatTeam2 = newGameLobby.chatTeam2
                    chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount-1)
                    chatAdapter.notifyDataSetChanged()
                }
            }
            if (gameLobby.turn != newGameLobby.turn) {
                gameLobby.turn = newGameLobby.turn
                gameActivity.changeBackGround()
            }
            gameLobby.turnPhase = newGameLobby.turnPhase
            gameLobby.words = newGameLobby.words
            gameLobby.captainIndex1 = newGameLobby.captainIndex1
            gameLobby.captainIndex2 = newGameLobby.captainIndex2
            gameLobby.hint1 = newGameLobby.hint1
            gameLobby.hint2 = newGameLobby.hint2
            gameLobby.clue = newGameLobby.clue
            gameLobby.winner = newGameLobby.winner
            for (member in gameLobby.members) {
                if (member.userId == userGame.userId) {
                    userGame.vote = member.vote
                }
            }
            if (gameLobby.winner != "no") {
                ended = false
                val iWon = userGame.team == gameLobby.winner
                val endGameFragment = EndGameFragment(iWon, userGame, gameLobby)
                endGameFragment.show(gameActivity.supportFragmentManager, "GameFragment->EndGameFragment")
            }
            updateBottomPart()
            updateRightPart()
            gameView.invalidate()
        }
    }

    private fun updateGameLobby() {
        dataBase.child("GameLobbies").child(gameLobby.lobbyId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newGameLobby = snapshot.getValue(GameLobby::class.java)
                if (newGameLobby != null) {
                    updateGameLobbyUI(newGameLobby)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}