package com.example.androidstudio.home

import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.Lobby
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.EnigmaService
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.home.profile.ProfileFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson


class MenuActivity : AppCompatActivity(), View.OnClickListener{

    private val dataBase = FirebaseDatabase.getInstance().reference

    private lateinit var user: User
    private var myLobby: Lobby = Lobby("", "", mutableListOf(), mutableListOf(), mutableListOf(), false)
    private lateinit var requestsTextView: TextView
    private lateinit var profileButton: ImageButton
    private lateinit var intentService: Intent
    private lateinit var exampleService: EnigmaService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val userString = intent.extras?.getString("user")
        val gson = Gson()
        user = gson.fromJson(userString, User::class.java)

        requestsTextView = findViewById(R.id.profile_notification_textView)
        updateUser(requestsTextView)

        profileButton = findViewById(R.id.button_profile)
        profileButton.setOnClickListener(this)

        val serverHandler = ServerHandler(applicationContext)
        exampleService = EnigmaService(serverHandler, myLobby = myLobby)
        intentService = Intent(this, exampleService::class.java)
        startService(intentService);
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

    private fun updateUser(notification: TextView) {
        dataBase.child("Users").child(user.userId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userUpdate = snapshot.getValue(User::class.java)
                if (userUpdate != null) {
                    user.username = userUpdate.username
                    user.friends = userUpdate.friends
                    user.pendingFriendRequests = userUpdate.pendingFriendRequests
                    user.pendingInviteRequests = userUpdate.pendingInviteRequests
                    if (user.pendingFriendRequests?.size!! > 0) {
                        notification.visibility = View.VISIBLE
                        notification.text = user.pendingFriendRequests?.size.toString()
                    } else {
                        notification.visibility = View.GONE
                    }
                    Log.i(Config.USER_TAG, "updateUser() $user")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getUser(): User {
        return user
    }

    fun getNotificationTextView(): TextView {
        return requestsTextView
    }

    fun setProfileImageButtonVisibility(visibility: Int) {
        profileButton.visibility = visibility
        if (visibility == View.GONE) {
            requestsTextView.visibility = visibility
        } else {
            if (user.pendingFriendRequests != null && user.pendingFriendRequests?.size!! > 0) {
                requestsTextView.visibility = View.VISIBLE
                requestsTextView.text = user.pendingFriendRequests!!.size.toString()
            }
        }
    }

    fun setLobby(lobby: Lobby) {
        myLobby = lobby
        exampleService.setLobby(lobby)
    }

    override fun onDestroy() {
        val intentOnUnbind = Intent()
        intentOnUnbind.putExtra("userId", user.userId)
        exampleService.onUnbind(intentOnUnbind)
        super.onDestroy()
    }
}