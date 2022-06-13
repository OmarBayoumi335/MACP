package com.example.androidstudio.game

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.adapters.ChatAdapter
import com.example.androidstudio.classes.adapters.ChatGameAdapter
import com.example.androidstudio.classes.types.Clue
import com.example.androidstudio.classes.types.GameLobby
import com.example.androidstudio.classes.types.Lobby
import com.example.androidstudio.classes.types.UserGame
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.EnigmaService
import com.example.androidstudio.classes.utils.ServerHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class GameActivity : AppCompatActivity(){

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatGameAdapter
    private lateinit var chatImageButton: ImageButton
    private lateinit var chatEditText: EditText
    private lateinit var views: ConstraintLayout
    private lateinit var myTurnCaptain: ConstraintLayout
    private lateinit var myTurnMember: ConstraintLayout
    private lateinit var opponentTurnMember: ConstraintLayout
    private lateinit var intentService: Intent
    private lateinit var exampleService: EnigmaService
    private lateinit var serverHandler: ServerHandler
    private var myGameLobby: GameLobby = GameLobby(
        "",
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        "",
        0,
        mutableListOf(),
        "",
        "",
        0,
        0,
        Clue(),
        ""
    )
    private var myUserGame: UserGame = UserGame("", "", "", 0, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        serverHandler = ServerHandler(applicationContext)
        views = findViewById(R.id.game_views)
        views.visibility = View.GONE
        myTurnCaptain = findViewById(R.id.game_bottom_part)
        myTurnMember = findViewById(R.id.game_bottom_part_member)
        opponentTurnMember = findViewById(R.id.game_bottom_part_opponent)

        // Chat
        chatRecyclerView = findViewById(R.id.game_chat_recyclerview)
        chatAdapter = ChatGameAdapter(myGameLobby, myUserGame, applicationContext)
        chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount-1)
        chatAdapter.notifyDataSetChanged()
        chatRecyclerView.adapter = chatAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(applicationContext).apply {
            stackFromEnd = true
            reverseLayout = false
        }
        chatEditText = findViewById(R.id.game_message_edittext)
        chatImageButton = findViewById(R.id.game_send_message_image_button)
        chatImageButton.setOnClickListener {
            sendMessage()
        }

        exampleService = EnigmaService(serverHandler, myGameLobby = myGameLobby)
        intentService = Intent(this, exampleService::class.java)
        startService(intentService);
    }

    fun setViewVisible() {
        views.visibility = View.VISIBLE
    }

    private fun updateBottomView(text: TextView, number: TextView, directions: TextView, clue: Clue) {
        // text
        text.text = clue.text
        // number
        number.text = if (clue.number == 0) {
            ""
        } else {
            clue.number.toString()
        }
        // directions
        var directionsText = ""
        for (direction in clue.directions) {
            directionsText = directionsText.plus(" $direction")
        }
        directions.text = if (directionsText == "") {
            directionsText
        } else {
            directionsText.substring(1)
        }
    }

    fun setViewMyTurnMember(clue: Clue, gamePhase: Int) {
        myTurnMember.visibility = View.VISIBLE
        myTurnCaptain.visibility = View.GONE
        opponentTurnMember.visibility = View.GONE
        val text = findViewById<TextView>(R.id.game_word_hint_member)
        val number = findViewById<TextView>(R.id.game_number_hint_member)
        val directions = findViewById<TextView>(R.id.game_direction_hint_member)
        if (gamePhase == 0) {
            updateBottomView(text, number, directions, Clue())
        } else {
            updateBottomView(text, number, directions, clue)
        }
    }

    fun setViewMyTurnCaptain() {
        myTurnMember.visibility = View.GONE
        myTurnCaptain.visibility = View.VISIBLE
        opponentTurnMember.visibility = View.GONE
        val text = findViewById<EditText>(R.id.game_word_hint)
        val number = findViewById<Button>(R.id.game_number_hint)
        val directions = findViewById<Button>(R.id.game_direction_hint)
        text.setText("")
        number.text = "-"
        directions.text = "-"
    }

    fun setViewOpponentTurn(clue: Clue, gamePhase: Int) {
        myTurnMember.visibility = View.GONE
        myTurnCaptain.visibility = View.GONE
        opponentTurnMember.visibility = View.VISIBLE
        val text = findViewById<TextView>(R.id.game_word_hint_opponent)
        val number = findViewById<TextView>(R.id.game_number_hint_opponent)
        val directions = findViewById<TextView>(R.id.game_direction_hint_opponent)
        if (gamePhase == 0) {
            updateBottomView(text, number, directions, Clue())
        } else {
            updateBottomView(text, number, directions, clue)
        }
    }

    fun setGameLobby(newGameLobby: GameLobby) {
        myGameLobby = newGameLobby
        exampleService.setGameLobby(myGameLobby)
    }

    fun setUserGame(newUserGame: UserGame) {
        myUserGame = newUserGame
    }

    fun updateChat() {
        chatAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        val intentOnUnbind = Intent()
        intentOnUnbind.putExtra("userId", myUserGame.userId)
        intentOnUnbind.putExtra("isGameLobby", true)
        exampleService.onUnbind(intentOnUnbind)
        super.onDestroy()
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
            chatImageButton.isClickable = false
            serverHandler.apiCall(
                Config.POST,
                Config.POST_SEND_MESSAGE_GAMELOBBY,
                userId = myUserGame.userId,
                gameLobbyId = myGameLobby.lobbyId,
                username = myUserGame.username,
                chatText = textToSend
            )
        }
        Log.i(Config.GAME_TAG, "message: ->$textToSend<-")
        chatEditText.text.clear()
    }
}