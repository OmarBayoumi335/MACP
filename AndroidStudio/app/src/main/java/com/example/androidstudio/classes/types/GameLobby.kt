package com.example.androidstudio.classes.types

data class GameLobby(var lobbyId: String,
                     var members: MutableList<UserGame>,
                     var chatTeam1: MutableList<Message>,
                     var chatTeam2: MutableList<Message>,
                     var turn: String,
                     var turnPhase: Int,
                     var words: MutableList<Word>,
                     var captainIndex1: String,
                     var captainIndex2: String,
                     var hint1: Int,
                     var hint2: Int,
                     var clue: Clue){

    constructor() : this(
        "",
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        "",
        0,
        mutableListOf(),
        "",
        "",
        0,
        0,
        Clue()
    )

    override fun toString(): String {
        return "GameLobby(lobbyId='$lobbyId', " +
                "members=$members, " +
                "chatTeam1=$chatTeam1, " +
                "chatTeam2=$chatTeam2, " +
                "turn='$turn', " +
                "turnPhase=$turnPhase, " +
                "words=$words, " +
                "captainIndex1='$captainIndex1', " +
                "captainIndex2='$captainIndex2', " +
                "hint1=$hint1, " +
                "hint2=$hint2, " +
                "clue=$clue)"
    }

}