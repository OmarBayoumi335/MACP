package com.example.androidstudio.classes.types

data class UserIdentification(var username: String,
                              var userId: String){
    override fun toString(): String {
        return "UserIdentification(username='$username', userId='$userId')"
    }
}