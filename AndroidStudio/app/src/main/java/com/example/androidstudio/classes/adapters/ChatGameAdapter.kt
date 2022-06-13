package com.example.androidstudio.classes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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

class ChatGameAdapter(gameLobby: GameLobby, userGame: UserGame, private val context: Context): RecyclerView.Adapter<ChatGameAdapter.ViewHolder>(){

    private var gameLobby: GameLobby
    private var userGame: UserGame

    init {
        this.gameLobby = gameLobby
        this.userGame = userGame
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView = itemView.findViewById(R.id.item_chat_username)
        var tvId: TextView = itemView.findViewById(R.id.item_chat_id)
        var tvText: TextView = itemView.findViewById(R.id.item_chat_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item_chat, parent, false)
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
//        if (user.userId == message.user.userId) {
//            tvText.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
//        }
    }

    override fun getItemCount(): Int {
        return if (userGame.team == context.resources.getString(R.string.team1)) {
            gameLobby.chatTeam1.size
        } else {
            gameLobby.chatTeam2.size
        }
    }
}