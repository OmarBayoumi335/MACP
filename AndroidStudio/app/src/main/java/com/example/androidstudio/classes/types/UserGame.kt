package com.example.androidstudio.classes.types

data class UserGame(var userId: String,
                    var userName: String,
                    var team1: Boolean,
                    var captain: Boolean){

    constructor() : this("", "", false, false)

    override fun toString(): String {
        return "UserGame(userId='$userId', userName='$userName', team1=$team1, captain=$captain)"
    }

}