package com.example.androidstudio.classi

import android.content.BroadcastReceiver

class Config {
    companion object{
        // Server
        val SERVER_BASE_URL = "http://192.168.1.73:5000/?"
        val POLLING_PERIOD = 1000L

        // Firebase/Google
        val CLIENT_ID = "8984037607-diqinm17j00uucdgkt14jb71seu6qlm1.apps.googleusercontent.com"

        // Tags
        val API = "Api request"
        val LOGINTAG = "Login"
        val ADDFRIENDTAG = "Add friend"

        // Api names
        // GET
        val GET_SEARCH_FRIEND = 0
        val GET_ID = 1
        val GET_USER_INFORMATION = 2
        val GET_FRIENDS_LIST = 3
        val GET_PENDING_FRIENDS_REQUEST = 4
        // PUT
        // POST
        val POST_SEND_FRIEND_REQUEST = 5
        val POST_CHANGE_NAME = 6
        val POST_ACCEPT_FRIEND_REQUEST = 8
        // DELETE
        val DELETE_FRIEND = 7
        val REJECT_FRIEND_REQUEST = 9
    }
}