package com.example.androidstudio.classes.utils


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
        val UPDATEUITAG = "UpdateUI"
        val LOBBYTAG = "Lobby"

        // Api names
        // GET
        val GET_SEARCH_FRIEND = 0
        val GET_USER_INFORMATION = 1
        val GET_FRIENDS_LIST = 2
        val GET_PENDING_FRIENDS_REQUEST = 3
        val GET_USER_EXIST = 9
        val GET_NUM_PENDING_FRIENDS_REQUEST = 11
        val GET_LOBBY_INVITES = 16
        val GET_NUM_LOBBY_INVITES = 17
        val GET_LOBBY_USERS = 18

        // PUT
        val PUT_NEW_USER = 10
        val PUT_NEW_LOBBY = 12

        // POST
        val POST_SEND_FRIEND_REQUEST = 4
        val POST_CHANGE_NAME = 5
        val POST_ACCEPT_FRIEND_REQUEST = 7
        val POST_SEND_LOBBY_INVITE = 13
        val POST_ENTER_IN_LOBBY = 15

        // DELETE
        val DELETE_REMOVE_FRIEND = 6
        val DELETE_REJECT_FRIEND_REQUEST = 8
        val DELETE_LOBBY_INVITE = 14
    }
}