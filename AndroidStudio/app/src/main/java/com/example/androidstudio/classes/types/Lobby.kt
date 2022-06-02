package com.example.androidstudio.classes.types

data class Lobby(var lobbyId: String,
                 var lobbyName: String,
                 var team1: MutableList<UserIdentification>,
                 var team2: MutableList<UserIdentification>,
                 var chat: MutableList<Message>){

    constructor() : this("", "", mutableListOf(), mutableListOf(), mutableListOf())

    override fun toString(): String {
        return "Lobby(lobbyId='$lobbyId', lobbyName='$lobbyName', team1=$team1, team2=$team2, chat=$chat)"
    }
}