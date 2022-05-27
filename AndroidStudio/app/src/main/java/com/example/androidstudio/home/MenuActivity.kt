package com.example.androidstudio.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.utils.UpdateUI
import com.example.androidstudio.home.profile.ProfileFragment

class MenuActivity : AppCompatActivity(), View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // UID
        val sharedPreferences = getSharedPreferences("lastGoogleId", Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("UID", "").toString()

        val requestsTextView = findViewById<TextView>(R.id.profile_notification_textView)
        val serverHandler = ServerHandler(this)
        UpdateUI.updateNotificationProfile(this, serverHandler, userid, requestsTextView)

        val profileButton = findViewById<ImageButton>(R.id.button_profile)
        profileButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_profile -> openProfile()
        }
    }

    private fun openProfile() {
        ProfileFragment().show(supportFragmentManager, "MenuActivity->Profile")
    }
}