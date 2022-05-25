package com.example.androidstudio.home

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstudio.R
import com.example.androidstudio.classi.Config
import com.example.androidstudio.classi.ServerHandler
import com.example.androidstudio.home.profile.ProfileFragment
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import org.json.JSONObject

class MenuActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var serverHandler: ServerHandler
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        serverHandler = ServerHandler(this)
//        serverHandler.poll("0", object : ServerHandler.VolleyCallBack {
//            override fun onSuccess(reply: JSONObject) {
//                Log.i(Config.API, "Icona profilo notifiche")
//            }
//        })

        val profileButton = findViewById<ImageButton>(R.id.button_profile)
        profileButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_profile -> openProfile()
        }
    }

    private fun openProfile() {
//        serverHandler.stopPoll()
        ProfileFragment().show(supportFragmentManager, "MenuActivity->Profile")
    }
}