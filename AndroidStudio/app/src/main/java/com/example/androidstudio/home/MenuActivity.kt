package com.example.androidstudio.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstudio.R
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.home.profile.ProfileFragment
import com.google.gson.Gson
import org.json.JSONObject


class MenuActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var user: User
    private lateinit var requestsTextView: TextView
    private lateinit var profileButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val userString = intent.extras?.getString("user")
        val gson = Gson()
        user = gson.fromJson(userString, User::class.java)

        requestsTextView = findViewById(R.id.profile_notification_textView)
        if (user.pendingFriendRequests != null) {
            requestsTextView.visibility = View.VISIBLE
            requestsTextView.text = user.pendingFriendRequests!!.size.toString()
        }
        val serverHandler = ServerHandler(this)
        updateUser(this, serverHandler, requestsTextView)

        profileButton = findViewById(R.id.button_profile)
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

    private fun updateUser(menuActivity: MenuActivity,
                           serverHandler: ServerHandler,
                           notification: TextView) {
        serverHandler.apiCall(
            Config.GET,
            Config.GET_USER,
            userId = user.userId,
            callBack = object : ServerHandler.VolleyCallBack {
                override fun onSuccess(reply: JSONObject?) {
                    val userJsonString = reply.toString()
                    val gson = Gson()
                    val userUpdate = gson.fromJson(userJsonString, User::class.java)
                    user.username = userUpdate.username
                    user.friends = userUpdate.friends
                    user.pendingFriendRequests = userUpdate.pendingFriendRequests
                    user.pendingInviteRequests = userUpdate.pendingInviteRequests
                    if (user.pendingFriendRequests != null) {
                        notification.visibility = View.VISIBLE
                        notification.text = user.pendingFriendRequests?.size.toString()
                    } else {
                        notification.visibility = View.GONE
                    }
                    if (!menuActivity.isFinishing) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            updateUser(
                                menuActivity,
                                serverHandler,
                                notification
                            )
                        }, Config.POLLING_PERIOD)
                    }
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
            if (user.pendingFriendRequests != null || user.pendingFriendRequests?.size == 0) {
                requestsTextView.visibility = View.VISIBLE
                requestsTextView.text = user.pendingFriendRequests!!.size.toString()
            }
        }
    }
}