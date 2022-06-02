package com.example.androidstudio.classes.types

data class UserIdentification(var username: String,
                              var userId: String){

    constructor(): this("","")

    override fun toString(): String {
        return "UserIdentification(username='$username', userId='$userId')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserIdentification

        if (username != other.username) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + userId.hashCode()
        return result
    }
}