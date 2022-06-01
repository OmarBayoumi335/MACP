package com.example.androidstudio.classes.utils

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.home.MenuActivity
import com.example.androidstudio.home.lobby.CreatePartyFragment
import com.example.androidstudio.home.lobby.InviteInPartyFragment
import com.example.androidstudio.home.lobby.PartyInvitationFragment
import com.example.androidstudio.home.profile.ProfileFragment
import com.google.gson.Gson
import org.json.JSONObject

class UpdateUI {
    companion object {
//        fun updateUser(menuActivity: MenuActivity,
//                       serverHandler: ServerHandler,
//                       user: User,
//                       notification: TextView) {
//            Log.i(Config.UPDATEUITAG, "updateUser() $user")
//            serverHandler.apiCall(
//                Config.GET,
//                Config.GET_USER,
//                userId = user.userId,
//                callBack = object : ServerHandler.VolleyCallBack {
//                    override fun onSuccess(reply: JSONObject?) {
//                        val userJsonString = reply.toString()
//                        val gson = Gson()
//                        val userUpdate = gson.fromJson(userJsonString, User::class.java)
//                        user.username = userUpdate.username
//                        user.friends = userUpdate.friends
//                        user.pendingFriendRequests = userUpdate.pendingFriendRequests
//                        user.pendingInviteRequests = userUpdate.pendingInviteRequests
////                        user.roomMaster = userUpdate.roomMaster
//                        if (user.pendingFriendRequests != null) {
//                            notification.visibility = View.VISIBLE
//                            notification.text = user.pendingFriendRequests?.size.toString()
//                        } else {
//                            notification.visibility = View.GONE
//                        }
//                        if (!menuActivity.isFinishing) {
//                            Handler(Looper.getMainLooper()).postDelayed({
//                                updateUser(
//                                    menuActivity,
//                                    serverHandler,
//                                    user,
//                                    notification
//                                )
//                            }, Config.POLLING_PERIOD)
//                        }
//                    }
//                })
//        }
    }
}