package com.example.androidstudio.game

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.Clue
import com.example.androidstudio.classes.utils.Config
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class GameActivity : AppCompatActivity(){

    private lateinit var views: ConstraintLayout
    private lateinit var myTurnCaptain: ConstraintLayout
    private lateinit var myTurnMember: ConstraintLayout
    private lateinit var opponentTurnMember: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        views = findViewById(R.id.game_views)
        views.visibility = View.GONE
        myTurnCaptain = findViewById(R.id.game_bottom_part)
        myTurnMember = findViewById(R.id.game_bottom_part_member)
        opponentTurnMember = findViewById(R.id.game_bottom_part_opponent)
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

    fun setViewMyTurnMember(clue: Clue) {
        myTurnMember.visibility = View.VISIBLE
        myTurnCaptain.visibility = View.GONE
        opponentTurnMember.visibility = View.GONE
        val text = findViewById<TextView>(R.id.game_word_hint_member)
        val number = findViewById<TextView>(R.id.game_number_hint_member)
        val directions = findViewById<TextView>(R.id.game_direction_hint_member)
        updateBottomView(text, number, directions, clue)
    }

    fun setViewMyTurnCaptain(clue: Clue) {
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

    fun setViewOpponentTurn(clue: Clue) {
        myTurnMember.visibility = View.GONE
        myTurnCaptain.visibility = View.GONE
        opponentTurnMember.visibility = View.VISIBLE
        val text = findViewById<TextView>(R.id.game_word_hint_opponent)
        val number = findViewById<TextView>(R.id.game_number_hint_opponent)
        val directions = findViewById<TextView>(R.id.game_direction_hint_opponent)
        updateBottomView(text, number, directions, clue)
    }
}