package com.example.androidstudio.classes.types

data class Message(var user: UserIdentification,
                   var text: String){

    constructor(): this(UserIdentification(), "")
    override fun toString(): String {
        return "Message(user=$user, text='$text')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (user != other.user) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 31 * result + text.hashCode()
        return result
    }
}