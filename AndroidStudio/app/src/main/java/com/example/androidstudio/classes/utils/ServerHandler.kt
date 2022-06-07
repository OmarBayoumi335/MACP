package com.example.androidstudio.classes.utils

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ServerHandler(context: Context?) {
    private val url = Config.SERVER_BASE_URL
    private lateinit var queue: RequestQueue

    init {
        if (context != null) {
            this.queue = Volley.newRequestQueue(context)
        }
    }

    constructor(): this(null)

    fun apiCall(callType: String,
                req: String,
                userId: String = "",
                googleUserId: String = "",
                friendId: String = "",
                lobbyId: String = "",
                lobbyName: String = "",
                newName: String = "",
                username: String = "",
                chatText: String = "",
                team: String = "",
                words: String = "",
                turn: String = "",
                gameLobbyId: String = "",
                callBack: VolleyCallBack? = null) {
        var requestMethod = 0
        val reqParsed = reqParser(
            req = req,
            userId = userId,
            googleUserId = googleUserId,
            friendId = friendId,
            lobbyId = lobbyId,
            lobbyName = lobbyName,
            newName = newName,
            username = username,
            chatText = chatText,
            team = team,
            words = words,
            turn = turn,
            gameLobbyId = gameLobbyId)
        if (callType == Config.GET) requestMethod = Request.Method.GET
        if (callType == Config.POST) requestMethod = Request.Method.POST
        if (callType == Config.PUT) requestMethod = Request.Method.PUT
        if (callType == Config.DELETE) requestMethod = Request.Method.DELETE
        call(requestMethod, callType, req, reqParsed, callBack)
    }

    private fun reqParser(req: String,
                          userId: String,
                          googleUserId: String,
                          friendId: String,
                          lobbyId: String,
                          lobbyName: String,
                          newName: String,
                          username: String,
                          chatText: String,
                          team: String = "",
                          words: String = "",
                          gameLobbyId: String = "",
                          turn: String = "",): String {
        var reqParsed = url.plus("req=$req")
        if (userId != "") reqParsed = reqParsed.plus("&userId=$userId")
        if (googleUserId != "") reqParsed = reqParsed.plus("&googleUserId=$googleUserId")
        if (friendId != "") reqParsed = reqParsed.plus("&friendId=$friendId")
        if (lobbyId != "") reqParsed = reqParsed.plus("&lobbyId=$lobbyId")
        if (lobbyName != "") reqParsed = reqParsed.plus("&lobbyName=$lobbyName")
        if (newName != "") reqParsed = reqParsed.plus("&newName=$newName")
        if (username != "") reqParsed = reqParsed.plus("&username=$username")
        if (chatText != "") reqParsed = reqParsed.plus("&chatText=$chatText")
        if (team != "") reqParsed = reqParsed.plus("&team=$team")
        if (words != "") reqParsed = reqParsed.plus("&words=$words")
        if (turn != "") reqParsed = reqParsed.plus("&turn=$turn")
        if (gameLobbyId != "") reqParsed = reqParsed.plus("&gameLobbyId=$gameLobbyId")
        return reqParsed
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
            Log.i(Config.API_TAG, "onSuccess ${stringMethod}, ${reqCode}: $reply")
            callBack?.onSuccess(reply)
        }, {
                error: VolleyError? ->
            Log.e(Config.API_TAG, "onError ${stringMethod}, ${reqCode}: " + error.toString())
        })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    interface VolleyCallBack {
        fun onSuccess(reply: JSONObject?)
    }
}