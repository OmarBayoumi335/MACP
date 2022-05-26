package com.example.androidstudio.classi

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


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
        val PROFILE = "Profile"
        val PERMISSIONTAG = "Permission"

        // Api names
        // GET
        val GET_SEARCH_FRIEND = 0
        val GET_ID = 1
        val GET_USER_INFORMATION = 2
        val GET_FRIENDS_LIST = 3
        val GET_PENDING_FRIENDS_REQUEST = 4
        val GET_USER_EXIST = 10
        // PUT
        val PUT_NEW_USER = 11
        // POST
        val POST_SEND_FRIEND_REQUEST = 5
        val POST_CHANGE_NAME = 6
        val POST_ACCEPT_FRIEND_REQUEST = 8
        // DELETE
        val DELETE_FRIEND = 7
        val REJECT_FRIEND_REQUEST = 9
    }
}