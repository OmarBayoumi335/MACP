package com.example.androidstudio.classes.types

open class User(username: String, id: String) {
    private var username: String
    private var id: String

    init {
        this.username = username
        this.id = id
    }

    fun getUsername(): String {
        return this.username
    }

    fun setUsername(name: String) {
        this.username = name
    }

    fun getId(): String {
        return this.id
    }

    fun setId(surname: String){
        this.id = surname
    }

    override fun toString(): String {
        return "User(username='$username', id='$id')"
    }

}