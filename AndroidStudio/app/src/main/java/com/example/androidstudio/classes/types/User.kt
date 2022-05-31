package com.example.androidstudio.classes.types

data class User(var username: String,
                var userId: String,
                var friends: MutableList<UserIdentification>?,
                var pendingFriendRequests: MutableList<UserIdentification>?,
                var pendingInviteRequests: MutableList<LobbyUser>?){
    override fun toString(): String {
        return "User(username='$username', userId='$userId', friends=$friends, pendingFriendRequests=$pendingFriendRequests, pendingInviteRequests=$pendingInviteRequests)"
    }
}