package com.example.androidstudio.classes.utils

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.androidstudio.classes.types.Lobby
import com.example.androidstudio.classes.types.User

class EnigmaService(myLobby: Lobby, serverHandler: ServerHandler) : Service() {
    private var startMode: Int = 0             // indicates how to behave if the service is killed
    private var binder: IBinder? = null        // interface for clients that bind
    private var allowRebind: Boolean = false   // indicates whether onRebind should be used
    private var myLobby: Lobby
    private val serverHandler: ServerHandler

    constructor(): this(Lobby(), ServerHandler())

    init {
        this.myLobby = myLobby
        this.serverHandler = serverHandler
    }

    fun setLobby(newLobby: Lobby) {
        this.myLobby = newLobby
    }

    override fun onCreate() {
        // The service is being created
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // The service is starting, due to a call to startService()
        return startMode
    }

    override fun onBind(intent: Intent): IBinder? {
        // A client is binding to the service with bindService()
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        // All clients have unbound with unbindService()
        val userId = intent.getStringExtra("userId")
        if (myLobby.lobbyId != "") {
            if (userId != null && !myLobby.start) {
                serverHandler.apiCall(
                    Config.DELETE,
                    Config.DELETE_LEAVE_LOBBY,
                    lobbyId = myLobby.lobbyId,
                    userId = userId
                )
            }
        }
        return allowRebind
    }

    override fun onRebind(intent: Intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    override fun onDestroy() {
        // The service is no longer used and is being destroyed
    }
}