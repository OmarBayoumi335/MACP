package com.example.androidstudio.classes.utils

import com.example.androidstudio.classes.types.User


class Config {
    companion object{
        // Server
        val SERVER_BASE_URL = "http://192.168.1.73:5000/?"
//                val SERVER_BASE_URL = "http://omir97.pythonanywhere.com/?"
//        val SERVER_BASE_URL = "http://192.168.1.88:5000/?"  //cristiano
        val POLLING_PERIOD = 800L

        // Firebase/Google
        val CLIENT_ID = "8984037607-diqinm17j00uucdgkt14jb71seu6qlm1.apps.googleusercontent.com"

        // Max lobby member
        val MAX_TEAM_MEMBERS = 8

        // Tags
        val API_TAG = "Api request"
        val LOGIN_TAG = "Login"
        val ADD_FRIEND_TAG = "Add friend"
        val PROFILE_TAG = "Profile"
        val PERMISSION_TAG = "Permission"
        val UPDATE_UI_TAG = "UpdateUI"
        val LOBBY_TAG = "Lobby"
        val USER_TAG = "User"
        val LOADING_GAME_TAG = "Loading"
        val GAME_TAG = "game"
        val GAME_VIEW_TAG = "view_game"
        val ERROR_TAG = "error"

        // Api names
        val GET = "get"
        val GET_USERNAME = "get0"
        val GET_USER_EXIST = "get1"
        val GET_USER = "get2"
        val GET_SEARCH_FRIEND = "get3"
        val GET_INVITABLE_USER = "get4"
        val GET_GAME_LOBBY_NUMBER_OF_MEMBERS = "get5"
        val GET_GAME_INFORMATION = "get6"

        val PUT = "put"
        val PUT_NEW_USER = "put0"
        val PUT_NEW_LOBBY = "put1"
        val PUT_NEW_GAME_LOBBY = "put2"

        val POST = "post"
        val POST_CHANGE_NAME = "post0"
        val POST_ACCEPT_FRIEND_REQUEST = "post1"
        val POST_SEND_FRIEND_REQUEST = "post2"
        val POST_SEND_LOBBY_INVITE = "post3"
        val POST_ACCEPT_LOBBY_INVITE = "post4"
        val POST_CHANGE_TEAM = "post5"
        val POST_SEND_MESSAGE = "post6"
        val POST_CHANGE_READY_STATUS = "post7"
        val POST_JOIN_GAME_LOBBY = "post8"
        val POST_SEND_CLUE = "post9"
        val POST_VOTE = "post10"
        val POST_SEND_MESSAGE_GAMELOBBY = "post11"

        val DELETE = "delete"
        val DELETE_REMOVE_FRIEND = "delete0"
        val DELETE_REMOVE_FRIEND_REQUEST = "delete1"
        val DELETE_LEAVE_LOBBY = "delete2"
        val DELETE_LOBBY_INVITE = "delete3"
        val DELETE_LOBBY = "delete4"
        val DELETE_LEAVE_GAMELOBBY = "delete5"
    }
}