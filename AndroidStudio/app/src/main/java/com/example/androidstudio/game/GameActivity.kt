package com.example.androidstudio.game

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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

    private lateinit var views: ConstraintLayout
    private lateinit var viewBackground: ConstraintLayout
    private lateinit var myTurnCaptain: LinearLayout
    private lateinit var myTurnMember: LinearLayout
    private lateinit var opponentTurnMember: LinearLayout
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
        viewBackground = findViewById(R.id.game_view_background)
        views.visibility = View.GONE
        myTurnCaptain = findViewById(R.id.game_bottom_part)
        myTurnMember = findViewById(R.id.game_bottom_part_member)
        opponentTurnMember = findViewById(R.id.game_bottom_part_opponent)

        exampleService = EnigmaService(serverHandler, myGameLobby = myGameLobby)
        intentService = Intent(this, exampleService::class.java)
        startService(intentService);
    }

    fun setViewVisible() {
        views.visibility = View.VISIBLE
    }

    private fun updateBottomView(text: TextView, number: TextView, directions: TextView, clue: Clue, team: String) {
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
        directions.visibility = if (directionsText == "") {
            View.GONE
        } else {
            View.VISIBLE
        }
        // backgrounds
        if (team == resources.getString(R.string.team1)) {
            text.background = ContextCompat.getDrawable(applicationContext, R.drawable.game_clue_button4)
            number.background = ContextCompat.getDrawable(applicationContext, R.drawable.game_clue_button5)
            directions.background = ContextCompat.getDrawable(applicationContext, R.drawable.game_clue_button4)
        } else {
            text.background = ContextCompat.getDrawable(applicationContext, R.drawable.game_clue_button4a)
            number.background = ContextCompat.getDrawable(applicationContext, R.drawable.game_clue_button5a)
            directions.background = ContextCompat.getDrawable(applicationContext, R.drawable.game_clue_button4a)
        }
    }

    fun setViewMyTurnMember(clue: Clue, gamePhase: Int, team: String) {
        myTurnMember.visibility = View.VISIBLE
        myTurnCaptain.visibility = View.GONE
        opponentTurnMember.visibility = View.GONE
        val text = findViewById<TextView>(R.id.game_word_hint_member)
        val number = findViewById<TextView>(R.id.game_number_hint_member)
        val directions = findViewById<TextView>(R.id.game_direction_hint_member)
        if (gamePhase == 0) {
            updateBottomView(text, number, directions, Clue(), team)
        } else {
            updateBottomView(text, number, directions, clue, team)
        }
    }

    fun setViewMyTurnCaptain() {
        myTurnMember.visibility = View.GONE
        myTurnCaptain.visibility = View.VISIBLE
        opponentTurnMember.visibility = View.GONE
        val text = findViewById<EditText>(R.id.game_word_hint)
        val number = findViewById<Button>(R.id.game_number_hint)
        val directions = findViewById<TextView>(R.id.game_direction_hint)
        text.setText("")
        number.text = "-"
        directions.text = "-"
    }

    fun setViewOpponentTurn(clue: Clue, gamePhase: Int, team: String) {
        myTurnMember.visibility = View.GONE
        myTurnCaptain.visibility = View.GONE
        opponentTurnMember.visibility = View.VISIBLE
        val text = findViewById<TextView>(R.id.game_word_hint_opponent)
        val number = findViewById<TextView>(R.id.game_number_hint_opponent)
        val directions = findViewById<TextView>(R.id.game_direction_hint_opponent)
        if (gamePhase == 0) {
            updateBottomView(text, number, directions, Clue(), team)
        } else {
            updateBottomView(text, number, directions, clue, team)
        }
    }

    fun setGameLobby(newGameLobby: GameLobby) {
        myGameLobby = newGameLobby
        exampleService.setGameLobby(myGameLobby)
        changeBackGround()
    }

    fun changeBackGround() {
        if (myGameLobby.turn == resources.getString(R.string.team1)) {
            viewBackground.background = ContextCompat.getDrawable(applicationContext, R.drawable.background_game_team1)
        } else {
            viewBackground.background = ContextCompat.getDrawable(applicationContext, R.drawable.background_game_team2)
        }
    }

    fun setUserGame(newUserGame: UserGame) {
        myUserGame = newUserGame
    }

    override fun onDestroy() {
        val intentOnUnbind = Intent()
        intentOnUnbind.putExtra("userId", myUserGame.userId)
        intentOnUnbind.putExtra("isGameLobby", true)
        exampleService.onUnbind(intentOnUnbind)
        super.onDestroy()
    }
}