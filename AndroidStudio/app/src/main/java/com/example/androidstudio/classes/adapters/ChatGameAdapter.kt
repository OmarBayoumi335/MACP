package com.example.androidstudio.classes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.*
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.home.profile.AddFriendFragment
import com.example.androidstudio.home.profile.FriendsFragment
import com.example.androidstudio.home.profile.ProfileFragment
import com.example.androidstudio.home.profile.RequestsFragment
import org.json.JSONObject

class ChatGameAdapter(gameLobby: GameLobby, userGame: UserGame, context: Context): RecyclerView.Adapter<ChatGameAdapter.ViewHolder>(){

    private var gameLobby: GameLobby
    private var userGame: UserGame
    private var context: Context

    init {
        this.gameLobby = gameLobby
        this.userGame = userGame
        this.context = context
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView = itemView.findViewById(R.id.item_chat_username_game)
        var tvId: TextView = itemView.findViewById(R.id.item_chat_id_game)
        var tvText: TextView = itemView.findViewById(R.id.item_chat_text_game)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item_chat_game, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message: Message = if (userGame.team == context.resources.getString(R.string.team1)) {
            gameLobby.chatTeam1[position]
        } else {
            gameLobby.chatTeam2[position]
        }
        // Set item views based on your views and data model
        val tvUsername = holder.tvUsername
        val tvId = holder.tvId
        val tvText = holder.tvText
        tvUsername.text = message.user.username
        tvId.text = message.user.userId
        tvText.text = message.text
        if (userGame.team == context.resources.getString(R.string.team1)) {
            tvUsername.setTextColor(ContextCompat.getColor(context, R.color.chat_game_team1_title))
        } else {
            tvUsername.setTextColor(ContextCompat.getColor(context, R.color.chat_game_team2_title))
        }
    }

    override fun getItemCount(): Int {
        return if (userGame.team == context.resources.getString(R.string.team1)) {
            gameLobby.chatTeam1.size
        } else {
            gameLobby.chatTeam2.size
        }
    }
}