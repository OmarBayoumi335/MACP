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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        views = findViewById(R.id.game_views)

    }

    fun setViewVisible() {
        views.visibility = View.VISIBLE
    }
}