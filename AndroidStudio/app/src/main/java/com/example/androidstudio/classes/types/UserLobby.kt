package com.example.androidstudio.classes.types

open class UserLobby(username: String, id: String, lobbyId: String) : User(username, id) {

    private var lobbyId: String

    init {
        this.lobbyId = lobbyId
    }

    fun getLobbyId(): String {
        return this.lobbyId
    }

    fun setLobbyId(lobbyId: String) {
        this.lobbyId = lobbyId
    }

    override fun toString(): String {
        return "UserLobby(username='${getUsername()}', id='${getId()}', lobbyId='$lobbyId')"
    }
}