package com.example.androidstudio.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.*
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.game.views.GameView
import com.example.androidstudio.game.views.GuessCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.json.JSONObject

class GameFragment : Fragment(), View.OnClickListener{

    private val dataBase = FirebaseDatabase.getInstance().reference
    private lateinit var serverHandler: ServerHandler
    private lateinit var gameLobby: GameLobby
    private lateinit var userGame: UserGame
    private lateinit var gameView: GameView
    private lateinit var buttonDirection: Button
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

    @SuppressLint("SetTextI18n")
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

        Log.i(Config.GAME_TAG, "\ngameLobby: $gameLobby\nuserGame: $userGame")

        gameActivity = requireActivity() as GameActivity

        // Disable Android back button
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            }
        gameActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // send message in chat
        val sendMessageButton = gameActivity.findViewById<ImageButton>(R.id.game_send_message_image_button)
        sendMessageButton.setOnClickListener(this)

        // pass lobby and user in the view
        gameView.gameLobby = gameLobby
        gameView.userGame = userGame

        // button to choose direction for the clue
        buttonDirection = gameActivity.findViewById(R.id.game_direction_hint)
        buttonDirection.setOnClickListener(this)

        // buttons to choose the number for the clue
        selectNumberHintLayout = gameActivity.findViewById(R.id.game_select_number_hint)
        buttonNumberHint = gameActivity.findViewById(R.id.game_number_hint)
        buttonNumberHint.setOnClickListener(this)
        // hint number 1
        buttonValue1 = gameActivity.findViewById(R.id.game_button_value_1)
        buttonValue1.setOnClickListener(this)
        // hint number 2
        buttonValue2 = gameActivity.findViewById(R.id.game_button_value_2)
        buttonValue2.setOnClickListener(this)
        // hint number 3
        buttonValue3 = gameActivity.findViewById(R.id.game_button_value_3)
        buttonValue3.setOnClickListener(this)
        // hint number 4
        buttonValue4 = gameActivity.findViewById(R.id.game_button_value_4)
        buttonValue4.setOnClickListener(this)
        // hint number 5
        buttonValue5 = gameActivity.findViewById(R.id.game_button_value_5)
        buttonValue5.setOnClickListener(this)
        // hint number 6
        buttonValue6 = gameActivity.findViewById(R.id.game_button_value_6)
        buttonValue6.setOnClickListener(this)

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
        giveClue.setOnClickListener(this)
        giveClue.isClickable = false

        // button for pass the game phase without voting
        buttonPass = gameActivity.findViewById(R.id.game_pass_hint_member)
        buttonPass.setOnClickListener(this)

        // set bottom part view
        updateBottomPart()

        // set right part view
        updateRightPart()

        // update game status
        updateGameLobby()
        return gameView
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.game_direction_hint -> showDirectionHintFragment()
            R.id.game_number_hint -> showNumbersHint()
            R.id.game_button_value_1 -> selectNumberHint("1")
            R.id.game_button_value_2 -> selectNumberHint("2")
            R.id.game_button_value_3 -> selectNumberHint("3")
            R.id.game_button_value_4 -> selectNumberHint("4")
            R.id.game_button_value_5 -> selectNumberHint("5")
            R.id.game_button_value_6 -> selectNumberHint("6")
            R.id.game_send_message_image_button -> sendMessage()
            R.id.game_confirm_hint -> giveClueToMembers()
            R.id.game_pass_hint_member -> passButton()
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
            ChooseDirectionFragment(gameLobby.hint1)
        } else {
            ChooseDirectionFragment(gameLobby.hint2)
        }
        chooseDirectionsFragment.show(gameActivity.supportFragmentManager, "GameFragment->ChooseDirectionFragment")
    }

    private fun sendMessage() {

    }

    private fun giveClueToMembers() {
        val text = gameWordHint.text.toString()
        val number = buttonNumberHint.text.toString().toInt()
        var directions = mutableListOf<String>()
        if (buttonDirection.text.toString() != "-") {
            directions = buttonDirection.text.toString().split(" ").toMutableList()
        }
        val clue = Clue(text, number, directions)
        giveClue.isClickable = false
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
            buttonPass.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            buttonPass.text = resources.getString(R.string.don_t_pass_button)
            buttonPass.isClickable = false
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

    private fun checkClue() {
        giveClue.isClickable = selectNumber && selectText
    }

    private fun updateBottomPart() {
        Log.i(Config.GAME_VIEW_TAG, gameLobby.toString())
        if (gameLobby.turn == userGame.team) { // my turn
            if ((userGame.userId != gameLobby.captainIndex1 && userGame.userId != gameLobby.captainIndex2)) { // not captain
                if (gameLobby.turnPhase == 0) {
                    gameActivity.setViewOpponentTurn(gameLobby.clue, gameLobby.turnPhase)
                } else {
                    gameActivity.setViewMyTurnMember(gameLobby.clue, gameLobby.turnPhase)
                }
            } else { // captain
                if (gameLobby.turnPhase == 1) {
                    gameActivity.setViewOpponentTurn(gameLobby.clue, gameLobby.turnPhase)
                } else {
                    gameActivity.setViewMyTurnCaptain()
                }
            }
        } else { // opponent turn
            gameActivity.setViewOpponentTurn(gameLobby.clue, gameLobby.turnPhase)
        }
        if (userGame.vote != 16) {
            buttonPass.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            buttonPass.text = resources.getString(R.string.pass_button)
            buttonPass.isClickable = true
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
        turn.text = "${gameLobby.turn}: $phase"

        // chat title
        val chatTitle = gameActivity.findViewById<TextView>(R.id.game_chat_team_title_textview)
        chatTitle.text = userGame.team
    }

    private fun updateGameLobbyUI(newGameLobby: GameLobby) {
        if (gameLobby != newGameLobby) {
            gameLobby.lobbyId = newGameLobby.lobbyId
            gameLobby.members = newGameLobby.members
            gameLobby.chatTeam1 = newGameLobby.chatTeam1
            gameLobby.chatTeam2 = newGameLobby.chatTeam2
            gameLobby.turn = newGameLobby.turn
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



//        val words = mutableListOf(
//            Word("importer", "green", "SOUTH"),
//            Word("esok", "gray", "NORTH"),
//            Word("morbidity", "green", "EAST"),
//            Word("kitsch", "red", "WEST"),
//            Word("tune", "green", "SOUTH"),
//            Word("haddock", "red", "SOUTH"),
//            Word("zookeeper", "green", "WEST"),
//            Word("ecologist", "gray", "NORTH"),
//            Word("fruitbat", "green", "SOUTH"),
//            Word("hen", "red", "NORTH"),
//            Word("salamander", "red", "EAST"),
//            Word("apatosaur", "gray", "SOUTH"),
//            Word("kerosene", "green", "SOUTH"),
//            Word("glowworm", "red", "NORTH"),
//            Word("factotum", "red", "SOUTH"),
//            Word("city", "black", "SOUTH")
//        )
//        gameView.gameLobby = GameLobby()
//        gameView.gameLobby.words = words
//        gameView.gameLobby.turn = mutableListOf("red", "green").random()
//        gameView.userGame = UserGame()
//        gameView.userGame.captain = false
//        gameView.userGame.team = mutableListOf("red", "green").random()
//
//        val turn = requireActivity().findViewById<TextView>(R.id.game_turn_team_textview)
//        turn.text = "${resources.getString(R.string.team)} ${gameView.gameLobby.turn}: ${resources.getString(R.string.turn)} ${gameView.gameLobby.turnNumber}"
//        val chatTitle = requireActivity().findViewById<TextView>(R.id.game_chat_team_title_textview)
//        chatTitle.text = "${resources.getString(R.string.team_chat)} ${gameView.userGame.team}"
//        val b = requireActivity().findViewById<ImageButton>(R.id.game_send_message_image_button)
//        b.setOnClickListener {
//            gameView.gameLobby.turn = if (gameView.gameLobby.turn == "Team Red") "Team Green" else "Team Red"
//            gameView.invalidate()
//        }