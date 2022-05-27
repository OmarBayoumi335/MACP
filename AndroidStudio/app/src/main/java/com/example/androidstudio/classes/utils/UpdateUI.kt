package com.example.androidstudio.classes.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.classes.adapters.ProfileFriendListAdapter
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.User
import com.example.androidstudio.classes.adapters.InviteFriendListAdapter
import com.example.androidstudio.classes.adapters.LobbyTeamAdapter
import com.example.androidstudio.home.MenuActivity
import com.example.androidstudio.home.lobby.CreatePartyFragment
import com.example.androidstudio.home.lobby.InviteInPartyFragment
import com.example.androidstudio.home.profile.ProfileFragment
import org.json.JSONObject

class UpdateUI {
    companion object {
        fun updateFriendsList(c: Context,
                              profileFragment: ProfileFragment,
                              serverHandler: ServerHandler,
                              userid: String,
                              profileFriendsRecyclerView: RecyclerView) {
            serverHandler.getFriendsList(userid, object : ServerHandler.VolleyCallBack {
                override fun onSuccess(reply: JSONObject?) {
                    var friendListView: List<User> = emptyList()
                    if (reply!!.get("friends").toString() != "null") {
                        val friends = reply.getJSONArray("friends")
                        val friendsNumber = friends.length()
                        for (i in 0 until friendsNumber) {
                            val id = friends[i].toString().substring(7, 15)
                            val username = friends[i].toString()
                                .substring(29, friends[i].toString().length - 2)
                            val friendUser = User(username, id)
                            friendListView += friendUser
                        }
                    }
                    val profileFriendListAdapter = ProfileFriendListAdapter(c, friendListView, 0, userid, serverHandler)
                    profileFriendsRecyclerView.adapter = profileFriendListAdapter
                    profileFriendsRecyclerView.layoutManager = LinearLayoutManager(c)
//                    Log.i(Config.UPDATEUITAG, "Update of friends view")
                    if (profileFragment.context != null) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            updateFriendsList(
                                c,
                                profileFragment,
                                serverHandler,
                                userid,
                                profileFriendsRecyclerView
                            )
                        }, Config.POLLING_PERIOD)
                    }
                }
            })
        }

        fun updatePendingList(c: Context,
                              profileFragment: ProfileFragment,
                              serverHandler: ServerHandler,
                              userid: String,
                              profileFriendsRecyclerView: RecyclerView) {
            serverHandler.getPendingFriendsRequest(userid, object : ServerHandler.VolleyCallBack {
                override fun onSuccess(reply: JSONObject?) {
                    var pendingListView: List<User> = emptyList()

                    if (reply!!.get("pendingFriendRequests").toString() != "null") {
                        val pendingRequests = reply.getJSONArray("pendingFriendRequests")
                        val pendingRequestsNumber = pendingRequests.length()
                        for (i in 0 until pendingRequestsNumber) {
                            val id = pendingRequests[i].toString().substring(7, 15)
                            val username = pendingRequests[i].toString()
                                .substring(29, pendingRequests[i].toString().length - 2)
                            val pendingUser = User(username, id)
                            pendingListView += pendingUser
                        }
                    }
                    val profileFriendListAdapter = ProfileFriendListAdapter(c, pendingListView, 1, userid, serverHandler)
                    profileFriendsRecyclerView.adapter = profileFriendListAdapter
                    profileFriendsRecyclerView.layoutManager = LinearLayoutManager(c)
//                    Log.i(Config.UPDATEUITAG, "Update of pending view")
                    if (profileFragment.context != null) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            updatePendingList(
                                c,
                                profileFragment,
                                serverHandler,
                                userid,
                                profileFriendsRecyclerView
                            )
                        }, Config.POLLING_PERIOD)
                    }
                }
            })
        }

        fun updateInviteFriendsList(c: Context,
                                    inviteInPartyFragment: InviteInPartyFragment,
                                    serverHandler: ServerHandler,
                                    userid: String,
                                    profileFriendsRecyclerView: RecyclerView) {
            serverHandler.getFriendsList(userid, object : ServerHandler.VolleyCallBack {
                override fun onSuccess(reply: JSONObject?) {
                    var friendListView: List<User> = emptyList()
                    if (reply!!.get("friends").toString() != "null") {
                        val friends = reply.getJSONArray("friends")
                        val friendsNumber = friends.length()
                        for (i in 0 until friendsNumber) {
                            val id = friends[i].toString().substring(7, 15)
                            val username = friends[i].toString()
                                .substring(29, friends[i].toString().length - 2)
                            val friendUser = User(username, id)
                            friendListView += friendUser
                        }
                    }
                    val profileFriendListAdapter = InviteFriendListAdapter(c, friendListView, serverHandler)
                    profileFriendsRecyclerView.adapter = profileFriendListAdapter
                    profileFriendsRecyclerView.layoutManager = LinearLayoutManager(c)
//                    Log.i(Config.UPDATEUITAG, "Update of friends view")
                    if (inviteInPartyFragment.context != null) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            updateInviteFriendsList(
                                c,
                                inviteInPartyFragment,
                                serverHandler,
                                userid,
                                profileFriendsRecyclerView
                            )
                        }, Config.POLLING_PERIOD)
                    }
                }
            })
        }

        fun updateNotificationInProfile(profileFragment: ProfileFragment,
                                        serverHandler: ServerHandler,
                                        userid: String,
                                        notification: TextView) {
            serverHandler.getNumPending(userid, object : ServerHandler.VolleyCallBack {
                override fun onSuccess(reply: JSONObject?) {
                    val numNotification = reply?.get("pendingRequestsNum").toString()
                    if (numNotification == "0") {
                        notification.visibility = View.GONE
                    } else {
                        notification.text = numNotification
                        notification.visibility = View.VISIBLE
                    }
                    if (profileFragment.context != null) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            updateNotificationInProfile(
                                profileFragment,
                                serverHandler,
                                userid,
                                notification
                            )
                        }, Config.POLLING_PERIOD)
                    }
                }
            })
        }

        fun updateTeamList(c: Context,
                           createPartyFragment: CreatePartyFragment,
                           serverHandler: ServerHandler,
                           userid: String,
                           profileFriendsRecyclerView: RecyclerView,
                           team1: Boolean) {
            serverHandler.getFriendsList(userid, object : ServerHandler.VolleyCallBack {
                override fun onSuccess(reply: JSONObject?) {
                    var friendListView: List<User> = emptyList()
                    if (reply!!.get("friends").toString() != "null") {
                        val friends = reply.getJSONArray("friends")
                        val friendsNumber = friends.length()
                        for (i in 0 until friendsNumber) {
                            val id = friends[i].toString().substring(7, 15)
                            val username = friends[i].toString()
                                .substring(29, friends[i].toString().length - 2)
                            val friendUser = User(username, id)
                            friendListView += friendUser
                        }
                    }
                    val profileFriendListAdapter = LobbyTeamAdapter(c, friendListView, serverHandler, team1)
                    profileFriendsRecyclerView.adapter = profileFriendListAdapter
                    profileFriendsRecyclerView.layoutManager = LinearLayoutManager(c)
//                    Log.i(Config.UPDATEUITAG, "Update of friends view")
                    if (createPartyFragment.context != null) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            updateTeamList(
                                c,
                                createPartyFragment,
                                serverHandler,
                                userid,
                                profileFriendsRecyclerView,
                                team1
                            )
                        }, Config.POLLING_PERIOD)
                    }
                }
            })
        }

        fun updateNotificationProfile(menuActivity: MenuActivity,
                                      serverHandler: ServerHandler,
                                      userid: String,
                                      notification: TextView) {
            serverHandler.getNumPending(userid, object : ServerHandler.VolleyCallBack {
                override fun onSuccess(reply: JSONObject?) {
                    val numNotification = reply?.get("pendingRequestsNum").toString()
                    if (numNotification == "0") {
                        notification.visibility = View.GONE
                    } else {
                        notification.text = numNotification
                        notification.visibility = View.VISIBLE
                    }
                    if (!menuActivity.isFinishing) {
//                        Log.i(Config.UPDATEUITAG, "Update menu")
                        Handler(Looper.getMainLooper()).postDelayed({
                            updateNotificationProfile(
                                menuActivity,
                                serverHandler,
                                userid,
                                notification
                            )
                        }, Config.POLLING_PERIOD)
                    }
                }
            })
        }
    }
}