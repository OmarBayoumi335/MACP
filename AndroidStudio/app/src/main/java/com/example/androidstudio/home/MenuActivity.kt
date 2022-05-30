package com.example.androidstudio.home

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavArgs
import androidx.navigation.NavArgument
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.UpdateUI
import com.example.androidstudio.home.profile.ProfileFragment
import com.google.gson.Gson


class MenuActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val userString = intent.extras?.getString("user")
        val gson = Gson()
        user = gson.fromJson(userString, User::class.java)

        val requestsTextView = findViewById<TextView>(R.id.profile_notification_textView)
        if (user.pendingFriendRequests != null) {
            requestsTextView.visibility = View.VISIBLE
            requestsTextView.text = user.pendingFriendRequests!!.size.toString()
        }
        val serverHandler = ServerHandler(this)
        UpdateUI.updateUser(this, serverHandler, user, requestsTextView)

        val profileButton = findViewById<ImageButton>(R.id.button_profile)
        profileButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_profile -> openProfile()
        }
    }

    private fun openProfile() {
        val p = ProfileFragment(user)
        p.show(supportFragmentManager, "MenuActivity->Profile")
    }

    fun getUser(): User {
        return user
    }
}