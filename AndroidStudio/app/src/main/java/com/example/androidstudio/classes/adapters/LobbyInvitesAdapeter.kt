package com.example.androidstudio.classes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.types.UserLobby

class LobbyInvitesAdapeter(private val c: Context,
                           private val mUser: List<UserLobby>,
                           private val uid: String,
                           private val serverHandler: ServerHandler): RecyclerView.Adapter<LobbyInvitesAdapeter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView = itemView.findViewById(R.id.lobby_invite_item_username_textview)
        var tvId: TextView = itemView.findViewById(R.id.lobby_invite_item_id_textview)
        var bAccept: ImageButton = itemView.findViewById(R.id.lobby_invite_positive_item_image_button)
        var bDecline: ImageButton = itemView.findViewById(R.id.lobby_invite_negative_item_image_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item_lobby_invite, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend: UserLobby = mUser[position]
        // Set item views based on your views and data model
        val tvUsername = holder.tvUsername
        val tvId = holder.tvId
        val bAccept = holder.bAccept
        val bDecline = holder.bDecline
        tvUsername.text = friend.getUsername()
        tvId.text = friend.getId()

        bAccept.setOnClickListener {
//            serverHandler.postSendLobbyInvite(uid, lobbyId)
        }

        bDecline.setOnClickListener {
//            serverHandler.postSendLobbyInvite(uid, lobbyId)
        }

    }

    override fun getItemCount(): Int {
        return mUser.size
    }
}