package com.example.androidstudio.classes.types

data class GameLobby(var lobbyId: String,
                     var members: MutableList<UserGame>,
                     var chatTeam1: MutableList<Message>,
                     var chatTeam2: MutableList<Message>,
                     var turnTeam1: Boolean){

    constructor() : this("", mutableListOf(), mutableListOf(), mutableListOf(), false)

    override fun toString(): String {
        return "GameLobby(lobbyId='$lobbyId', members=$members, chatTeam1=$chatTeam1, chatTeam2=$chatTeam2, turnTeam1=$turnTeam1)"
    }

}