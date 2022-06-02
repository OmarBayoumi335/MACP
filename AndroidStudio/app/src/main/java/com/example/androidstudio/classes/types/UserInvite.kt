package com.example.androidstudio.classes.types

data class UserInvite(var username: String,
                      var userId: String,
                      var lobbyId: String,
                      var lobbyName: String){


    constructor(): this("", "", "", "")
    override fun toString(): String {
        return "UserInvite(username='$username', userId='$userId', lobbyId='$lobbyId', lobbyName='$lobbyName')"
    }
}