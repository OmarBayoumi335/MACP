package com.example.androidstudio.classes.types

data class Message(var user: UserIdentification,
                   var text: String,
                   var date: String){
    override fun toString(): String {
        return "Message(user=$user, text='$text', date='$date')"
    }
}