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
        fun updateFriendsList(c: Context,
                              profileFragment: ProfileFragment,
                              serverHandler: ServerHandler,
                              userid: String,
                              profileFriendsRecyclerView: RecyclerView) {
//            serverHandler.getFriendsList(userid, object : ServerHandler.VolleyCallBack {
//                override fun onSuccess(reply: JSONObject?) {
//                    var friendListView: List<User> = emptyList()
//                    if (reply!!.get("friends").toString() != "null") {
//                        val friends = reply.getJSONArray("friends")
//                        val friendsNumber = friends.length()
//                        for (i in 0 until friendsNumber) {
//                            val id = friends[i].toString().substring(7, 15)
//                            val username = friends[i].toString()
//                                .substring(29, friends[i].toString().length - 2)
//                            val friendUser = User(username, id)
//                            friendListView += friendUser
//                        }
//                    }
//                    val profileFriendListAdapter = ProfileFriendListAdapter(c, friendListView, 0, userid, serverHandler)
//                    profileFriendsRecyclerView.adapter = profileFriendListAdapter
//                    profileFriendsRecyclerView.layoutManager = LinearLayoutManager(c)
////                    Log.i(Config.UPDATEUITAG, "Update of friends view")
//                    if (profileFragment.context != null) {
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            updateFriendsList(
//                                c,
//                                profileFragment,
//                                serverHandler,
//                                userid,
//                                profileFriendsRecyclerView
//                            )
//                        }, Config.POLLING_PERIOD)
//                    }
//                }
//            })
        }

        fun updatePendingList(c: Context,
                              profileFragment: ProfileFragment,
                              serverHandler: ServerHandler,
                              userid: String,
                              profileFriendsRecyclerView: RecyclerView) {
//            serverHandler.getPendingFriendsRequest(userid, object : ServerHandler.VolleyCallBack {
//                override fun onSuccess(reply: JSONObject?) {
//                    var pendingListView: List<User> = emptyList()
//
//                    if (reply!!.get("pendingFriendRequests").toString() != "null") {
//                        val pendingRequests = reply.getJSONArray("pendingFriendRequests")
//                        val pendingRequestsNumber = pendingRequests.length()
//                        for (i in 0 until pendingRequestsNumber) {
//                            val id = pendingRequests[i].toString().substring(7, 15)
//                            val username = pendingRequests[i].toString()
//                                .substring(29, pendingRequests[i].toString().length - 2)
//                            val pendingUser = User(username, id)
//                            pendingListView += pendingUser
//                        }
//                    }
//                    val profileFriendListAdapter = ProfileFriendListAdapter(c, pendingListView, 1, userid, serverHandler)
//                    profileFriendsRecyclerView.adapter = profileFriendListAdapter
//                    profileFriendsRecyclerView.layoutManager = LinearLayoutManager(c)
////                    Log.i(Config.UPDATEUITAG, "Update of pending view")
//                    if (profileFragment.context != null) {
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            updatePendingList(
//                                c,
//                                profileFragment,
//                                serverHandler,
//                                userid,
//                                profileFriendsRecyclerView
//                            )
//                        }, Config.POLLING_PERIOD)
//                    }
//                }
//            })
        }

        fun updateInviteFriendsList(c: Context,
                                    inviteInPartyFragment: InviteInPartyFragment,
                                    serverHandler: ServerHandler,
                                    userid: String,
                                    profileFriendsRecyclerView: RecyclerView,
                                    lobbyId: String) {
//            serverHandler.getFriendsList(userid, object : ServerHandler.VolleyCallBack {
//                override fun onSuccess(reply: JSONObject?) {
//                    var friendListView: List<User> = emptyList()
//                    if (reply!!.get("friends").toString() != "null") {
//                        val friends = reply.getJSONArray("friends")
//                        val friendsNumber = friends.length()
//                        for (i in 0 until friendsNumber) {
//                            val id = friends[i].toString().substring(7, 15)
//                            val username = friends[i].toString()
//                                .substring(29, friends[i].toString().length - 2)
//                            val friendUser = User(username, id)
//                            friendListView += friendUser
//                        }
//                    }
//                    val profileFriendListAdapter = InviteFriendListAdapter(c, friendListView, userid, serverHandler, lobbyId)
//                    profileFriendsRecyclerView.adapter = profileFriendListAdapter
//                    profileFriendsRecyclerView.layoutManager = LinearLayoutManager(c)
////                    Log.i(Config.UPDATEUITAG, "Update of friends view")
//                    if (inviteInPartyFragment.context != null) {
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            updateInviteFriendsList(
//                                c,
//                                inviteInPartyFragment,
//                                serverHandler,
//                                userid,
//                                profileFriendsRecyclerView,
//                                lobbyId
//                            )
//                        }, Config.POLLING_PERIOD)
//                    }
//                }
//            })
        }



        fun updateTeamList(c: Context,
                           createPartyFragment: CreatePartyFragment,
                           serverHandler: ServerHandler,
                           userid: String,
                           profileFriendsRecyclerView: RecyclerView,
                           team1: Boolean) {
//            serverHandler.getFriendsList(userid, object : ServerHandler.VolleyCallBack {
//                override fun onSuccess(reply: JSONObject?) {
//                    var friendListView: List<User> = emptyList()
//                    if (reply!!.get("friends").toString() != "null") {
//                        val friends = reply.getJSONArray("friends")
//                        val friendsNumber = friends.length()
//                        for (i in 0 until friendsNumber) {
//                            val id = friends[i].toString().substring(7, 15)
//                            val username = friends[i].toString()
//                                .substring(29, friends[i].toString().length - 2)
//                            val friendUser = User(username, id)
//                            friendListView += friendUser
//                        }
//                    }
//                    val profileFriendListAdapter = LobbyTeamAdapter(c, friendListView, serverHandler, team1)
//                    profileFriendsRecyclerView.adapter = profileFriendListAdapter
//                    profileFriendsRecyclerView.layoutManager = LinearLayoutManager(c)
////                    Log.i(Config.UPDATEUITAG, "Update of friends view")
//                    if (createPartyFragment.context != null) {
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            updateTeamList(
//                                c,
//                                createPartyFragment,
//                                serverHandler,
//                                userid,
//                                profileFriendsRecyclerView,
//                                team1
//                            )
//                        }, Config.POLLING_PERIOD)
//                    }
//                }
//            })
        }



        fun updateInviteList(c: Context,
                             partyInvitationFragment: PartyInvitationFragment,
                             serverHandler: ServerHandler,
                             userid: String,
                             profileFriendsRecyclerView: RecyclerView) {
//            serverHandler.getLobbyInvites(userid, object : ServerHandler.VolleyCallBack {
//                override fun onSuccess(reply: JSONObject?) {
//                    val lobbyId = ""
//                    var invitesListView: List<UserLobby> = emptyList()
//                    if (reply!!.get("lobbyInvites").toString() != "null") {
//                        val invites = reply.getJSONArray("lobbyInvites")
//                        val invitesNumber = invites.length()
//                        for (i in 0 until invitesNumber) {
//                            val attributes = invites[i].toString().split(",")
//                            val id = attributes[0].substring(7, attributes[0].length-1)
//                            val lobbyId = attributes[1].substring(11, attributes[1].length-1)
//                            val username = attributes[2].substring(12, attributes[2].length-2)
//                            val userLobby = UserLobby(username, id, lobbyId)
//                            invitesListView += userLobby
//                        }
//                    }
//                    val profileFriendListAdapter = LobbyInvitesAdapeter(c, invitesListView, userid, serverHandler)
//                    profileFriendsRecyclerView.adapter = profileFriendListAdapter
//                    profileFriendsRecyclerView.layoutManager = LinearLayoutManager(c)
////                    Log.i(Config.UPDATEUITAG, "Update of friends view")
//                    if (partyInvitationFragment.context != null) {
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            updateInviteList(
//                                c,
//                                partyInvitationFragment,
//                                serverHandler,
//                                userid,
//                                profileFriendsRecyclerView
//                            )
//                        }, Config.POLLING_PERIOD)
//                    }
//                }
//            })
        }

        fun updateNotificationProfile(menuActivity: MenuActivity,
                                      serverHandler: ServerHandler,
                                      user: User,
                                      notification: TextView) {
//            serverHandler.apiCall(
//                Config.GET,
//                Config.GET_PENDING_REQUESTS,
//                userId = user.userId,
//                callBack = object : ServerHandler.VolleyCallBack {
//                    override fun onSuccess(reply: JSONObject?) {
//                        val pendingRequests: MutableList<User> = reply?.get("requests") as MutableList<User>
//                        Log.i(Config.UPDATEUITAG, "updateNotificationProfile() $user")
//                        if (numNotification == 0) {
//                            notification.visibility = View.GONE
//                        } else {
//                            notification.text = numNotification.toString()
//                            notification.visibility = View.VISIBLE
//                        }
//                        Log.i(Config.UPDATEUITAG, user.toString())
//                        if (!menuActivity.isFinishing) {
//                            Handler(Looper.getMainLooper()).postDelayed({
//                                updateNotificationProfile(
//                                    menuActivity,
//                                    serverHandler,
//                                    user,
//                                    notification
//                                )
//                            }, Config.POLLING_PERIOD)
//                        }
//                    }
//            })
        }
        fun updateUser(menuActivity: MenuActivity,
                       serverHandler: ServerHandler,
                       user: User,
                       notification: TextView) {
            Log.i(Config.UPDATEUITAG, "updateUser() $user")
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
                        user.roomMaster = userUpdate.roomMaster
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
                                    user,
                                    notification
                                )
                            }, Config.POLLING_PERIOD)
                        }
                    }
                })
        }
    }
}