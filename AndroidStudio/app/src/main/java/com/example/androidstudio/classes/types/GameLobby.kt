package com.example.androidstudio.classes.types

data class GameLobby(var lobbyId: String,
                     var members: MutableList<UserGame>,
                     var chatTeam1: MutableList<Message>,
                     var chatTeam2: MutableList<Message>,
                     var turn: String,
                     var turnNumber: Int,
                     var words: MutableList<Word>){

    constructor() : this("", mutableListOf(), mutableListOf(), mutableListOf(), "", 0, mutableListOf())

    override fun toString(): String {
        return "GameLobby(lobbyId='$lobbyId', members=$members, chatTeam1=$chatTeam1, chatTeam2=$chatTeam2, turn=$turn, turnNumber=$turnNumber, words=$words)"
    }

}