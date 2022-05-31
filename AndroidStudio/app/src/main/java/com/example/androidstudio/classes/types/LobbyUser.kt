package com.example.androidstudio.classes.types

data class LobbyUser(var username: String,
                     var userId: String,
                     var lobbyId: String,
                     var lobbyName: String){
    override fun toString(): String {
        return "LobbyUser(username='$username', userId='$userId', lobbyId='$lobbyId', lobbyName='$lobbyName')"
    }
}