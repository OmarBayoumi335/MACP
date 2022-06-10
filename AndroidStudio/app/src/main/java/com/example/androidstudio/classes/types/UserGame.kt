package com.example.androidstudio.classes.types

data class UserGame(var userId: String,
                    var username: String,
                    var team: String,
                    var ready: Boolean){

    constructor() : this("", "", "", false)

    override fun toString(): String {
        return "UserGame(userId='$userId', userName='$username', team1=$team, ready=$ready)"
    }

}