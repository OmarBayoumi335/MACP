package com.example.androidstudio.classi

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import org.json.JSONObject

class ServerHandler(context: Context) {
    private val url = Config.SERVER_BASE_URL
    private val pollingPeriod = Config.POLLING_PERIOD
    private val queue = Volley.newRequestQueue(context)

//    private var h = Handler(Looper.myLooper()!!)
//    private lateinit var r : Runnable

//    fun poll(request: String, callBack: VolleyCallBack) {
//        r = Runnable{
//            get(request, callBack)
//            poll(request, callBack)
//        }
//        h.postDelayed(r,
//            this.pollingPeriod)
//    }
//
//    fun stopPoll() {
//        h.removeCallbacks(r)
//    }

    fun getUserInformation(userId: String, callBack: VolleyCallBack) {
        val stringRequest = StringRequest(
            Request.Method.GET, url
                    + "req=" + Config.GET_USER_INFORMATION
                    + "&userId=" + userId,{
                    response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
                Log.i(Config.API, "get user information onSuccess: ${reply.toString(2)}")
                callBack.onSuccess(reply)
            }, {
                    error: VolleyError? ->
                Log.e(Config.API, "get user information error: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun getId(callBack: VolleyCallBack) {
        val stringRequest = StringRequest(
            Request.Method.GET, url
                    + "req=" + Config.GET_ID,{
                    response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
                Log.i(Config.API, "get id onSuccess: ${reply.toString(2)}")
                callBack.onSuccess(reply)
            }, {
                    error: VolleyError? ->
                Log.e(Config.API, "get id error: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun getSearchFriend(userId: String, friendId: String, callBack: VolleyCallBack) {
        val stringRequest = StringRequest(
            Request.Method.GET, url
                    + "req=" + Config.GET_SEARCH_FRIEND
                    + "&userId=" + userId
                    + "&friendId=" + "%23".plus(friendId.substring(1)), {
                    response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
                Log.i(Config.API, "get search friend onSuccess: ${reply.toString(2)}")
                callBack.onSuccess(reply)
            }, {
                    error: VolleyError? ->
                Log.e(Config.API, "get search friend error: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun getFriendsList(userId: String, callBack: VolleyCallBack) {
        val stringRequest = StringRequest(
            Request.Method.GET, url
                    + "req=" + Config.GET_FRIENDS_LIST
                    + "&userId=" + userId, {
                    response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
                Log.i(Config.API, "get friends list onSuccess: ${reply.toString(2)}")
                callBack.onSuccess(reply)
            }, {
                    error: VolleyError? ->
                Log.e(Config.API, "get friends list error: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun getPendingFriendsRequest(userId: String, callBack: VolleyCallBack) {
        val stringRequest = StringRequest(
            Request.Method.GET, url
                    + "req=" + Config.GET_PENDING_FRIENDS_REQUEST
                    + "&userId=" + userId, {
                    response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
                Log.i(Config.API, "get pending friends list onSuccess: ${reply.toString(2)}")
                callBack.onSuccess(reply)
            }, {
                    error: VolleyError? ->
                Log.e(Config.API, "get pending friends list error: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun getUserExist(userId: String, callBack: VolleyCallBack) {
        val stringRequest = StringRequest(
            Request.Method.GET, url
                    + "req=" + Config.GET_USER_EXIST
                    + "&userId=" + userId, {
                    response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
                Log.i(Config.API, "get user exist onSuccess: ${reply.toString(2)}")
                callBack.onSuccess(reply)
            }, {
                    error: VolleyError? ->
                Log.e(Config.API, "get user exist error: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun postChangeName(userId: String, newName: String) {
        val stringRequest = StringRequest(
            Request.Method.POST, url
                    + "req=" + Config.POST_CHANGE_NAME
                    + "&userId=" + userId
                    + "&newName=" + newName,{
                    response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
                Log.i(Config.API, "post change name onSuccess: ${reply.toString(2)}")
            }, {
                    error: VolleyError? ->
                Log.e(Config.API, "getUserInformation error: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun postSendFriendRequest(userId: String, friendId: String) {
        val stringRequest = StringRequest(
            Request.Method.POST, url
                    + "req=" + Config.POST_SEND_FRIEND_REQUEST
                    + "&userId=" + userId
                    + "&friendId=" + "%23".plus(friendId.substring(1)),{
                    response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
                Log.i(Config.API, "post change name onSuccess: ${reply.toString(2)}")
            }, {
                    error: VolleyError? ->
                Log.e(Config.API, "getUserInformation error: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun putNewUser(userId: String, username: String, id: String) {
        val stringRequest = StringRequest(
            Request.Method.PUT, url
                    + "req=" + Config.PUT_NEW_USER
                    + "&userId=" + userId
                    + "&username=" + username
                    + "&id=" + "%23".plus(id.substring(1)),{
                    response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
                Log.i(Config.API, "post change name onSuccess: ${reply.toString(2)}")
            }, {
                    error: VolleyError? ->
                Log.e(Config.API, "getUserInformation error: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    interface VolleyCallBack {
        fun onSuccess(reply: JSONObject?)
    }
}