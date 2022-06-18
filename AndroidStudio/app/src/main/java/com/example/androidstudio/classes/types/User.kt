package com.example.androidstudio.classes.types

data class User(var username: String,
                var userId: String,
                var friends: MutableList<UserIdentification>?,
                var pendingFriendRequests: MutableList<UserIdentification>?,
                var pendingInviteRequests: MutableList<UserInvite>?){

    constructor(): this("", "", mutableListOf(), mutableListOf(), mutableListOf())

    override fun toString(): String {
        return "User(username='$username', userId='$userId', friends=$friends, pendingFriendRequests=$pendingFriendRequests, pendingInviteRequests=$pendingInviteRequests)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (username != other.username) return false
        if (userId != other.userId) return false
        if (friends != other.friends) return false
        if (pendingFriendRequests != other.pendingFriendRequests) return false
        if (pendingInviteRequests != other.pendingInviteRequests) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + (friends?.hashCode() ?: 0)
        result = 31 * result + (pendingFriendRequests?.hashCode() ?: 0)
        result = 31 * result + (pendingInviteRequests?.hashCode() ?: 0)
        return result
    }
}