package com.example.androidstudio.classes.types

data class Lobby(var lobbyId: String,
                 var lobbyName: String,
                 var team1: MutableList<LobbyUser>,
                 var team2: MutableList<LobbyUser>,
                 var chat: MutableList<Message>){
    override fun toString(): String {
        return "Lobby(lobbyId='$lobbyId', lobbyName='$lobbyName', team1=$team1, team2=$team2, chat=$chat)"
    }
}