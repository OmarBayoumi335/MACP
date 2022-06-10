package com.example.androidstudio.game

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.androidstudio.R
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
        myTurnCaptain = findViewById(R.id.game_bottom_part)
        myTurnMember = findViewById(R.id.game_bottom_part_member)
        opponentTurnMember = findViewById(R.id.game_bottom_part_opponent)
    }

    fun setViewVisible() {
        views.visibility = View.VISIBLE
    }

    fun setViewMyTurnMember() {
        myTurnMember.visibility = View.VISIBLE
        myTurnCaptain.visibility = View.GONE
    }

    fun setViewMyTurnCaptain() {
        myTurnMember.visibility = View.GONE
        myTurnCaptain.visibility = View.VISIBLE
    }

    fun setViewOpponentTurn() {
        myTurnMember.visibility = View.GONE
        myTurnCaptain.visibility = View.GONE
        opponentTurnMember.visibility = View.VISIBLE
    }
}