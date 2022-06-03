package com.example.androidstudio.classes.types

data class UserLobby(var username: String,
                     var userId: String,
                     var ready: Boolean){

    constructor(): this("","", false)

    override fun toString(): String {
        return "UserLobby(username='$username', userId='$userId', ready=$ready)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserLobby

        if (username != other.username) return false
        if (userId != other.userId) return false
        if (ready != other.ready) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + ready.hashCode()
        return result
    }
}