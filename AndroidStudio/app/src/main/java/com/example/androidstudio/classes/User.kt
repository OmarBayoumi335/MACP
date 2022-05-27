package com.example.androidstudio.classes

class User(username: String, id: String) {
    private var username: String
    private var id: String
    constructor() : this("", "#0000000")

    init {
        this.username = username
        this.id = id
    }

    fun getUsername(): String {
        return username
    }

    fun setUsername(name: String) {
        this.username = name
    }

    fun getId(): String {
        return id
    }

    fun setId(surname: String){
        this.id = surname
    }

    override fun toString(): String {
        return "User(username='$username', id='$id')"
    }

}