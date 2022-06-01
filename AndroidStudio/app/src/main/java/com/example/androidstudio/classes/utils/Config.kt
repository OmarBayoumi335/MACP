package com.example.androidstudio.classes.utils

import com.example.androidstudio.classes.types.User


class Config {
    companion object{
        // Server
        //val SERVER_BASE_URL = "http://192.168.1.73:5000/?"
        //        val SERVER_BASE_URL = "http://omir97.pythonanywhere.com/?"
        val SERVER_BASE_URL = "http://192.168.1.88:5000/?"  //cristiano
        val POLLING_PERIOD = 4500L

        // Firebase/Google
        val CLIENT_ID = "8984037607-diqinm17j00uucdgkt14jb71seu6qlm1.apps.googleusercontent.com"

        // Tags
        val API = "Api request"
        val LOGINTAG = "Login"
        val ADDFRIENDTAG = "Add friend"
        val PROFILETAG = "Profile"
        val PERMISSIONTAG = "Permission"
        val UPDATEUITAG = "UpdateUI"
        val LOBBYTAG = "Lobby"

        // Api names
        val GET = "get"
        val GET_USERNAME = "get0"
        val GET_USER_EXIST = "get1"
        val GET_USER = "get2"
        val GET_PENDING_REQUESTS = "get3"
        val GET_SEARCH_FRIEND = "get4"
        val GET_INVITABLE_USER = "get5"
//        val GET_USERNAME = 1
//        val GET_FRIENDS_LIST = 2
//        val GET_PENDING_FRIENDS_REQUEST = 3
//        val GET_USER_EXIST = 9
//        val GET_NUM_PENDING_FRIENDS_REQUEST = 11
//        val GET_LOBBY_INVITES = 16
//        val GET_NUM_LOBBY_INVITES = 17
//        val GET_LOBBY_USERS = 18
//        val GET_ID = 20

        val PUT = "put"
        val PUT_NEW_USER = "put0"
        val PUT_NEW_LOBBY = "put1"

        val POST = "post"
        val POST_CHANGE_NAME = "post0"
        val POST_ACCEPT_FRIEND_REQUEST = "post1"
        val POST_SEND_FRIEND_REQUEST = "post2"
        val POST_SEND_LOBBY_INVITE = "post3"
        val POST_ACCEPT_LOBBY_INVITE = "post4"
//        val POST_SEND_LOBBY_INVITE = 13
//        val POST_ENTER_IN_LOBBY = 15

        val DELETE = "delete"
        val DELETE_REMOVE_FRIEND = "delete0"
        val DELETE_REMOVE_FRIEND_REQUEST = "delete1"
        val DELETE_LEAVE_LOBBY = "delete2"


//        val DELETE_REMOVE_FRIEND = 6
//        val DELETE_REJECT_FRIEND_REQUEST = 8
//        val DELETE_LOBBY_INVITE = 14


    }
}