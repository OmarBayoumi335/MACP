package com.example.androidstudio.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.GameLobby
import com.example.androidstudio.classes.types.UserGame
import com.example.androidstudio.classes.types.Word
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.game.views.GameView
import com.google.gson.Gson

class GameFragment : Fragment() {

    private lateinit var serverHandler: ServerHandler
    private lateinit var gameLobby: GameLobby
    private lateinit var userGame: UserGame
    private lateinit var gameView: GameView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        gameView = GameView(requireContext())
        gameView.activity = requireActivity()
//        serverHandler = ServerHandler(requireContext())
//        val gson = Gson()
//
//        // Game lobby info
//        val gameLobbyString = arguments?.getString("gameLobby")
//        gameLobby = gson.fromJson(gameLobbyString, GameLobby::class.java)
//
//        // User in game info
//        val userGameString = arguments?.getString("userGame")
//        userGame = gson.fromJson(userGameString, UserGame::class.java)
//
//        Log.i(Config.GAME_TAG, "\ngameLobby: $gameLobby\nuserGame: $userGame")
//
//        // Disable Android back button
//        val callback: OnBackPressedCallback =
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//
//                }
//            }
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
//        gameView.gameLobby = gameLobby
//        gameView.userGame = userGame


        val words = mutableListOf(
            Word("importer", "green", "SOUTH"),
            Word("esok", "gray", "NORTH"),
            Word("morbidity", "green", "EAST"),
            Word("kitsch", "red", "WEST"),
            Word("tune", "green", "SOUTH"),
            Word("haddock", "red", "SOUTH"),
            Word("zookeeper", "green", "WEST"),
            Word("ecologist", "gray", "NORTH"),
            Word("fruitbat", "green", "SOUTH"),
            Word("hen", "red", "NORTH"),
            Word("salamander", "red", "EAST"),
            Word("apatosaur", "gray", "SOUTH"),
            Word("kerosene", "green", "SOUTH"),
            Word("glowworm", "red", "NORTH"),
            Word("factotum", "red", "SOUTH"),
            Word("city", "black", "SOUTH")
        )
        gameView.gameLobby = GameLobby()
        gameView.gameLobby.words = words
        gameView.gameLobby.turn = mutableListOf("red", "green").random()
        gameView.userGame = UserGame()
        gameView.userGame.captain = true
        gameView.userGame.team = mutableListOf("red", "green").random()

        val turn = requireActivity().findViewById<TextView>(R.id.game_turn_team_textview)
        turn.text = "${resources.getString(R.string.team)} ${gameView.gameLobby.turn}: ${resources.getString(R.string.turn)} ${gameView.gameLobby.turnNumber}"
        val chatTitle = requireActivity().findViewById<TextView>(R.id.game_chat_team_title_textview)
        chatTitle.text = "${resources.getString(R.string.team_chat)} ${gameView.userGame.team}"
        val b = requireActivity().findViewById<ImageButton>(R.id.game_send_message_image_button)
        b.setOnClickListener {
            gameView.gameLobby.turn = if (gameView.gameLobby.turn == "red") "green" else "red"
            gameView.invalidate()
        }



        return gameView
    }
}