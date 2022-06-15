package com.example.androidstudio.classes.types

data class Message(var user: UserIdentification,
                   var text: String,
                   var color: String){

    constructor(): this(UserIdentification(), "", "")
    override fun toString(): String {
        return "Message(user=$user, text='$text')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (user != other.user) return false
        if (text != other.text) return false
        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }

}