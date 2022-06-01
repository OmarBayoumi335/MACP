package com.example.androidstudio.classes.utils

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ServerHandler(context: Context) {
    private val url = Config.SERVER_BASE_URL
    private val pollingPeriod = Config.POLLING_PERIOD
    private val queue = Volley.newRequestQueue(context)

    fun apiCall(callType: String,
                req: String,
                userId: String = "",
                googleUserId: String = "",
                friendId: String = "",
                lobbyId: String = "",
                lobbyName: String = "",
                newName: String = "",
                username: String = "",
                callBack: VolleyCallBack? = null) {
        var requestMethod = 0
        val reqParsed = reqParser(req, userId, googleUserId, friendId, lobbyId, lobbyName, newName, username)
        if (callType == Config.GET) requestMethod = Request.Method.GET
        if (callType == Config.POST) requestMethod = Request.Method.POST
        if (callType == Config.PUT) requestMethod = Request.Method.PUT
        if (callType == Config.DELETE) requestMethod = Request.Method.DELETE
        call(requestMethod, callType, req, reqParsed, callBack)
    }

    private fun call(requestMethod: Int,
                     stringMethod: String,
                     reqCode: String,
                     req: String,
                     callBack: VolleyCallBack? = null) {
        val stringRequest = StringRequest(requestMethod, req, {
                response ->
            // Display the first 500 characters of the response string.
            val reply = JSONObject(response.toString())
            Log.i(Config.API, "onSuccess ${stringMethod}, ${reqCode}: $reply")
            callBack?.onSuccess(reply)
        }, {
                error: VolleyError? ->
            Log.e(Config.API, "onError ${stringMethod}, ${reqCode}: " + error.toString())
        })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun reqParser(req: String,
                          userId: String,
                          googleUserId: String,
                          friendId: String,
                          lobbyId: String,
                          lobbyName: String,
                          newName: String,
                          username: String): String {
        var reqParsed = url.plus("req=$req")
        if (userId != "") reqParsed = reqParsed.plus("&userId=$userId")
        if (googleUserId != "") reqParsed = reqParsed.plus("&googleUserId=$googleUserId")
        if (friendId != "") reqParsed = reqParsed.plus("&friendId=$friendId")
        if (lobbyId != "") reqParsed = reqParsed.plus("&lobbyId=$lobbyId")
        if (lobbyName != "") reqParsed = reqParsed.plus("&lobbyName=$lobbyName")
        if (newName != "") reqParsed = reqParsed.plus("&newName=$newName")
        if (username != "") reqParsed = reqParsed.plus("&username=$username")
        return reqParsed
    }

    interface VolleyCallBack {
        fun onSuccess(reply: JSONObject?)
    }

//    fun getUserInformation(userId: String, callBack: VolleyCallBack) {
//        val stringRequest = StringRequest(
//            Request.Method.GET, url
//                    + "req=" + Config.GET_USER_INFORMATION
//                    + "&userId=" + userId,{
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "get user information onSuccess: $reply")
//                callBack.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "get user information error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun getSearchFriend(userId: String, friendId: String, callBack: VolleyCallBack) {
//        val stringRequest = StringRequest(
//            Request.Method.GET, url
//                    + "req=" + Config.GET_SEARCH_FRIEND
//                    + "&userId=" + userId
//                    + "&friendId=" + "%23".plus(friendId.substring(1)), {
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "get search friend onSuccess: $reply")
//                callBack.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "get search friend error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun getFriendsList(userId: String, callBack: VolleyCallBack) {
//        val stringRequest = StringRequest(
//            Request.Method.GET, url
//                    + "req=" + Config.GET_FRIENDS_LIST
//                    + "&userId=" + userId, {
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "get friends list onSuccess: $reply")
//                callBack.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "get friends list error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun getPendingFriendsRequest(userId: String, callBack: VolleyCallBack) {
//        val stringRequest = StringRequest(
//            Request.Method.GET, url
//                    + "req=" + Config.GET_PENDING_FRIENDS_REQUEST
//                    + "&userId=" + userId, {
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "get pending friends list onSuccess: $reply")
//                callBack.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "get pending friends list error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun getUserExist(googleUserId: String, callBack: VolleyCallBack) {
//        val stringRequest = StringRequest(
//            Request.Method.GET, url
//                    + "req=" + Config.GET_USER_EXIST
//                    + "&googleUserId=" + googleUserId, {
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "get user exist onSuccess: $reply")
//                callBack.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "get user exist error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun getNumPending(userId: String, callBack: VolleyCallBack) {
//        val stringRequest = StringRequest(
//            Request.Method.GET, url
//                    + "req=" + Config.GET_NUM_PENDING_FRIENDS_REQUEST
//                    + "&userId=" + userId, {
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "get num pending onSuccess: $reply")
//                callBack.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "get num pending error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun getLobbyInvites(userId: String, callBack: VolleyCallBack) {
//        val stringRequest = StringRequest(
//            Request.Method.GET, url
//                    + "req=" + Config.GET_LOBBY_INVITES
//                    + "&userId=" + userId, {
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "get lobby invites onSuccess: $reply")
//                callBack.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "get lobby invites error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun getLobbyUsers(userId: String, callBack: VolleyCallBack) {
//        val stringRequest = StringRequest(
//            Request.Method.GET, url
//                    + "req=" + Config.GET_LOBBY_USERS
//                    + "&userId=" + userId, {
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "get lobby users onSuccess: $reply")
//                callBack.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "get lobby users error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun postChangeName(userId: String, newName: String) {
//        val stringRequest = StringRequest(
//            Request.Method.POST, url
//                    + "req=" + Config.POST_CHANGE_NAME
//                    + "&userId=" + userId
//                    + "&newName=" + newName,{
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "post change name onSuccess: $reply")
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "get change name error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun postSendFriendRequest(userId: String, friendId: String) {
//        val stringRequest = StringRequest(
//            Request.Method.POST, url
//                    + "req=" + Config.POST_SEND_FRIEND_REQUEST
//                    + "&userId=" + userId
//                    + "&friendId=" + "%23".plus(friendId.substring(1)),{
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "post send friend request onSuccess: $reply")
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "get send friend request error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun postAcceptFriendRequest(userId: String, friendId: String) {
//        val stringRequest = StringRequest(
//            Request.Method.POST, url
//                    + "req=" + Config.POST_ACCEPT_FRIEND_REQUEST
//                    + "&userId=" + userId
//                    + "&friendId=" + "%23".plus(friendId.substring(1)),{
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "post accept friend request onSuccess: $reply")
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "get accept friend request error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun postSendLobbyInvite(userId: String, friendId: String, lobbyId: String, callBack: VolleyCallBack) {
//        val stringRequest = StringRequest(
//            Request.Method.POST, url
//                    + "req=" + Config.POST_SEND_LOBBY_INVITE
//                    + "&userId=" + userId
//                    + "&friendId=" + "%23".plus(friendId.substring(1))
//                    + "&lobbyId=" + lobbyId,{
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "post send lobby invite onSuccess: $reply")
//                callBack.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "get send lobby invite error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun putNewUser(googleUserId: String, username: String) {
//        val stringRequest = StringRequest(
//            Request.Method.PUT, url
//                    + "req=" + Config.PUT_NEW_USER
//                    + "&googleUserId=" + googleUserId
//                    + "&username=" + username,{
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "put new user onSuccess: $reply")
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "put new user error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun putNewLobby(userId: String, lobbyId: String) {
//        val stringRequest = StringRequest(
//            Request.Method.PUT, url
//                    + "req=" + Config.PUT_NEW_LOBBY
//                    + "&userId=" + userId
//                    + "&lobbyId=" + lobbyId,{
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "put new lobby onSuccess: $reply")
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "put new lobby error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun deleteRejectFriendRequest(userId: String, friendId: String) {
//        val stringRequest = StringRequest(
//            Request.Method.DELETE, url
//                    + "req=" + Config.DELETE_REJECT_FRIEND_REQUEST
//                    + "&userId=" + userId
//                    + "&friendId=" + "%23".plus(friendId.substring(1)),{
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "delete reject friend request onSuccess: $reply")
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "delete reject friend request error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    fun deleteFriend(userId: String, friendId: String) {
//        val stringRequest = StringRequest(
//            Request.Method.DELETE, url
//                    + "req=" + Config.DELETE_REMOVE_FRIEND
//                    + "&userId=" + userId
//                    + "&friendId=" + "%23".plus(friendId.substring(1)),{
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "delete friend onSuccess: $reply")
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "delete friend error: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }

//    private fun get(req: String,
//                    callBack: VolleyCallBack? = null) {
//        val stringRequest = StringRequest(
//            Request.Method.GET, req, {
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "req get ${req}: $reply")
//                callBack?.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "req get ${req}: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    private fun post(req: String,
//                     callBack: VolleyCallBack? = null) {
//        val stringRequest = StringRequest(
//            Request.Method.POST, req, {
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "req post ${req}: $reply")
//                callBack?.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "req post ${req}: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    private fun put(req: String,
//                    callBack: VolleyCallBack? = null) {
//        val stringRequest = StringRequest(
//            Request.Method.PUT, req, {
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "req put ${req}: $reply")
//                callBack?.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "req put ${req}: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
//
//    private fun delete(req: String,
//                       callBack: VolleyCallBack? = null) {
//        val stringRequest = StringRequest(
//            Request.Method.DELETE, req, {
//                    response ->
//                // Display the first 500 characters of the response string.
//                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "req post ${req}: $reply")
//                callBack?.onSuccess(reply)
//            }, {
//                    error: VolleyError? ->
//                Log.e(Config.API, "req post ${req}: " + error.toString())
//            })
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }
}