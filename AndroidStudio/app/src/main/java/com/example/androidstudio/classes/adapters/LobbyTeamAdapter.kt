package com.example.androidstudio.classes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.Lobby
import com.example.androidstudio.classes.types.UserIdentification

class LobbyTeamAdapter(lobby: Lobby,
                       private val team1: Boolean): RecyclerView.Adapter<LobbyTeamAdapter.ViewHolder>(){

    private var lobby: Lobby

    init {
        this.lobby = lobby
    }

    inner class ViewHolder(itemView: View, team1: Boolean) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView
        var tvId: TextView
        init {
            if (team1) {
                tvUsername = itemView.findViewById(R.id.team1_member_username_textview)
                tvId = itemView.findViewById(R.id.team1_member_id_textview)
            } else {
                tvUsername = itemView.findViewById(R.id.team2_member_username_textview)
                tvId = itemView.findViewById(R.id.team2_member_id_textview)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView: View = if (team1) {
            inflater.inflate(R.layout.item_team1_member, parent, false)
        } else {
            inflater.inflate(R.layout.item_team2_member, parent, false)
        }
        return ViewHolder(contactView, team1)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend: UserIdentification = if (team1) {
            lobby.team1[position]
        } else {
            lobby.team2[position]
        }
        // Set item views based on your views and data model
        val tvUsername = holder.tvUsername
        val tvId = holder.tvId
        tvUsername.text = friend.username
        tvId.text = friend.userId

    }

    override fun getItemCount(): Int {
        return if (team1) lobby.team1.size
        else lobby.team2.size
    }
}