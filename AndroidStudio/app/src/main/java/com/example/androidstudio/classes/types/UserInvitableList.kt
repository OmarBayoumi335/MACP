package com.example.androidstudio.classes.types

data class UserInvitableList(var userList: MutableList<UserInvitable>){

    constructor(): this(mutableListOf())
    override fun toString(): String {
        return "UserInvitableList(userList=$userList)"
    }
}