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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameLobby

        if (lobbyId != other.lobbyId) return false
        if (members != other.members) return false
        if (chatTeam1 != other.chatTeam1) return false
        if (chatTeam2 != other.chatTeam2) return false
        if (turn != other.turn) return false
        if (turnPhase != other.turnPhase) return false
        if (words != other.words) return false
        if (captainIndex1 != other.captainIndex1) return false
        if (captainIndex2 != other.captainIndex2) return false
        if (hint1 != other.hint1) return false
        if (hint2 != other.hint2) return false
        if (clue != other.clue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lobbyId.hashCode()
        result = 31 * result + members.hashCode()
        result = 31 * result + chatTeam1.hashCode()
        result = 31 * result + chatTeam2.hashCode()
        result = 31 * result + turn.hashCode()
        result = 31 * result + turnPhase
        result = 31 * result + words.hashCode()
        result = 31 * result + captainIndex1.hashCode()
        result = 31 * result + captainIndex2.hashCode()
        result = 31 * result + hint1
        result = 31 * result + hint2
        result = 31 * result + clue.hashCode()
        return result
    }

}