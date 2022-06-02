package com.example.androidstudio.classes.types

data class Message(var user: UserIdentification,
                   var text: String){
    override fun toString(): String {
        return "Message(user=$user, text='$text')"
    }
}