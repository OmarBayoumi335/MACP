package com.example.androidstudio.classes.types

data class UserGame(var userId: String,
                    var username: String,
                    var team: String,
                    var vote: Int,
                    var ready: Boolean){

    constructor() : this("", "", "", 0, false)

    override fun toString(): String {
        return "UserGame(userId='$userId', username='$username', team='$team', vote=$vote, ready=$ready)"
    }

}