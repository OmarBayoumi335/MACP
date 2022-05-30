package com.example.androidstudio.classes.types

data class User(var username: String,
                var userId: String,
                var friends: MutableList<User>?,
                var pendingFriendRequests: MutableList<User>?,
                var pendingInviteRequests: MutableList<User>?,
                var roomMaster: Boolean){
    override fun toString(): String {
        return "User(username='$username', id='$userId', friends=$friends, pendingFriendRequests=$pendingFriendRequests, pendingInviteRequests=$pendingInviteRequests, roomMaster=$roomMaster)"
    }
}