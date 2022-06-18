package com.example.androidstudio.classes.types

data class UserIdentification(var username: String,
                              var userId: String,
                              var invitable: Boolean){

    constructor(): this("","", true)

    override fun toString(): String {
        return "UserIdentification(username='$username', userId='$userId', invitable=$invitable)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserIdentification

        if (username != other.username) return false
        if (userId != other.userId) return false
        if (invitable != other.invitable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + invitable.hashCode()
        return result
    }
}