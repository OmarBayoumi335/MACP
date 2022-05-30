package com.example.androidstudio.classes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.types.User

class LobbyTeamAdapter(private val c: Context,
                       private val mUser: List<User>,
//                              private val uid: String,
                       private val serverHandler: ServerHandler,
                       private val team1: Boolean): RecyclerView.Adapter<LobbyTeamAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View, team1: Boolean) : RecyclerView.ViewHolder(itemView) {
        lateinit var tvUsername: TextView
        lateinit var tvId: TextView

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
        var contactView: View = if (team1) {
            inflater.inflate(R.layout.item_team1_member, parent, false)
        } else {
            inflater.inflate(R.layout.item_team2_member, parent, false)
        }
        return ViewHolder(contactView, team1)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend: User = mUser[position]
        // Set item views based on your views and data model
        val tvUsername = holder.tvUsername
        val tvId = holder.tvId
        tvUsername.text = friend.username
        tvId.text = friend.userId

    }

    override fun getItemCount(): Int {
        return mUser.size
    }
}