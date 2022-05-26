package com.example.androidstudio.classi

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                    val profileFriendListAdapter = ProfileFriendListAdapter(friendListView)
                    profileFriendsRecyclerView.adapter = profileFriendListAdapter
                    profileFriendsRecyclerView.layoutManager = LinearLayoutManager(c)
                    Log.i(Config.PROFILE, "Update of friends view")
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
                    val profileFriendListAdapter = ProfileFriendListAdapter(pendingListView)
                    profileFriendsRecyclerView.adapter = profileFriendListAdapter
                    profileFriendsRecyclerView.layoutManager = LinearLayoutManager(c)
                    Log.i(Config.PROFILE, "Update of pending view")
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
    }
}