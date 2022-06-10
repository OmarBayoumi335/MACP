package com.example.androidstudio.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.GameLobby
import com.example.androidstudio.classes.types.UserGame
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.game.views.GameView
import com.google.gson.Gson

class GameFragment : Fragment(), View.OnClickListener{

    private lateinit var serverHandler: ServerHandler
    private lateinit var gameLobby: GameLobby
    private lateinit var userGame: UserGame
    private lateinit var gameView: GameView
    private lateinit var buttonDirection: Button
    private lateinit var buttonNumberHint: Button
    private lateinit var selectNumberHintLayout: ConstraintLayout
    private lateinit var buttonValue1: Button
    private lateinit var buttonValue2: Button
    private lateinit var buttonValue3: Button
    private lateinit var buttonValue4: Button
    private lateinit var buttonValue5: Button
    private lateinit var buttonValue6: Button
    private lateinit var gameActivity: GameActivity

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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // set bottom part view
        if (gameLobby.turn == userGame.team) { // my turn
            if (userGame.userId != gameLobby.captainIndex1 && userGame.userId != gameLobby.captainIndex2) { // not captain
                gameActivity.setViewMyTurnMember()
            } else {
                gameActivity.setViewMyTurnCaptain()
            }
        } else { // opponent turn
            gameActivity.setViewOpponentTurn()
        }

        // pass lobby and user in the view
        gameView.gameLobby = gameLobby
        gameView.userGame = userGame

        // turn title
        val turn = requireActivity().findViewById<TextView>(R.id.game_turn_team_textview)
        turn.text = "${resources.getString(R.string.team)} ${gameView.gameLobby.turn}: ${resources.getString(R.string.turn)} ${gameView.gameLobby.turnNumber}"

        // chat title
        val chatTitle = requireActivity().findViewById<TextView>(R.id.game_chat_team_title_textview)
        chatTitle.text = "${resources.getString(R.string.team_chat)} ${gameView.userGame.team}"

        // send message in chat
        val sendMessageButton = requireActivity().findViewById<ImageButton>(R.id.game_send_message_image_button)
        sendMessageButton.setOnClickListener(this)


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
//            gameView.gameLobby.turn = if (gameView.gameLobby.turn == "red") "green" else "red"
//            gameView.invalidate()
//        }

        buttonDirection = gameActivity.findViewById(R.id.game_direction_hint)
        buttonDirection.setOnClickListener(this)
        selectNumberHintLayout = gameActivity.findViewById(R.id.game_select_number_hint)

        buttonNumberHint = gameActivity.findViewById(R.id.game_number_hint)
        buttonNumberHint.setOnClickListener(this)

        buttonValue1 = gameActivity.findViewById(R.id.game_button_value_1)
        buttonValue1.setOnClickListener(this)

        buttonValue2 = gameActivity.findViewById(R.id.game_button_value_2)
        buttonValue2.setOnClickListener(this)

        buttonValue3 = gameActivity.findViewById(R.id.game_button_value_3)
        buttonValue3.setOnClickListener(this)

        buttonValue4 = gameActivity.findViewById(R.id.game_button_value_4)
        buttonValue4.setOnClickListener(this)

        buttonValue5 = gameActivity.findViewById(R.id.game_button_value_5)
        buttonValue5.setOnClickListener(this)

        buttonValue6 = gameActivity.findViewById(R.id.game_button_value_6)
        buttonValue6.setOnClickListener(this)

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
        }
    }

    private fun selectNumberHint(value: String){
        selectNumberHintLayout.visibility = View.GONE
        buttonNumberHint.text = value
    }

    private fun showNumbersHint(){
        if (selectNumberHintLayout.visibility == View.VISIBLE) {
            selectNumberHintLayout.visibility = View.GONE
        } else {
            selectNumberHintLayout.visibility = View.VISIBLE
        }
    }

    private fun showDirectionHintFragment(){
        val chooseDirectionsFragment = ChooseDirectionFragment()
        chooseDirectionsFragment.show(gameActivity.supportFragmentManager, "GameFragment->ChooseDirectionFragment")
    }

    private fun sendMessage() {
        TODO("Not yet implemented")
    }
}