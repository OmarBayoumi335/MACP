package com.example.androidstudio.classes.types

data class UserInvitableList(var userList: MutableList<UserInvitable>){

    constructor(): this(mutableListOf())
    override fun toString(): String {
        return "UserInvitableList(userList=$userList)"
    }
}

data class UserInvitable(var username: String,
                         var userId: String,
                         var status: String){

    constructor(): this("", "", "")
    override fun toString(): String {
        return "UserInvitable(username='$username', userId='$userId', status='$status')"
    }
}