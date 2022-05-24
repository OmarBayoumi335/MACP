package com.example.androidstudio.classi

class User(username: String, id: String) {
    private var username: String
    private var id: String
    private var friends: List<User>
    private var pendingFriendRequests: List<User>
    constructor() : this("", "#0000000")

    init {
        this.username = username
        this.id = id
        this.friends = emptyList()
        this.pendingFriendRequests = emptyList()
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

    fun getFriends(): List<User> {
        return friends
    }

    fun setFriends(friends: List<User>){
        this.friends = friends
    }

    fun getPendingFriendRequests(): List<User> {
        return pendingFriendRequests
    }

    fun setPendingFriendRequests(pendingFriendRequests: List<User>){
        this.pendingFriendRequests = pendingFriendRequests
    }

    fun addFriend(friend: User){
        this.friends += friend
    }

    fun addPendingFriendRequest(friend: User){
        this.pendingFriendRequests += friend
    }

    override fun toString(): String {
        return "User(username='$username', id='$id', friends=$friends)"
    }

}