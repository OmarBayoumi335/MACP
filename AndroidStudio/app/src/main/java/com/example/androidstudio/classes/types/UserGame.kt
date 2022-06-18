package com.example.androidstudio.classes.types

data class UserGame(var userId: String,
                    var username: String,
                    var team: String,
                    var vote: Int){

    constructor() : this("", "", "", 0)

    override fun toString(): String {
        return "UserGame(userId='$userId', username='$username', team='$team', vote=$vote)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserGame

        if (userId != other.userId) return false
        if (username != other.username) return false
        if (team != other.team) return false
        if (vote != other.vote) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + team.hashCode()
        result = 31 * result + vote
        return result
    }

}