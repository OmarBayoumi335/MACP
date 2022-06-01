package com.example.androidstudio.classes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.types.UserInvite
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.home.lobby.PartyInvitationFragment

class LobbyInvitesAdapter(
    user: User,
    private val partyInvitationFragment: PartyInvitationFragment,
    private val serverHandler: ServerHandler
    ): RecyclerView.Adapter<LobbyInvitesAdapter.ViewHolder>(){

    private var user: User

    init {
        this.user = user
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView = itemView.findViewById(R.id.lobby_invite_item_username_textview)
        var tvId: TextView = itemView.findViewById(R.id.lobby_invite_item_id_textview)
        var tvLobbyName: TextView = itemView.findViewById(R.id.lobby_invite_item_lobby_name)
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
        val friend: UserInvite = user.pendingInviteRequests!![position]
        val tvUsername = holder.tvUsername
        val tvId = holder.tvId
        val tvLobbyName = holder.tvLobbyName
        val acceptButton = holder.bAccept
        val declineButton = holder.bDecline

        tvUsername.text = friend.username
        tvId.text = friend.userId
        tvLobbyName.text = friend.lobbyName

        acceptButton.setOnClickListener {
            user.pendingInviteRequests!!.remove(user.pendingInviteRequests!![position])
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, user.pendingInviteRequests!!.size)
            val bundle = bundleOf("lobbyId" to friend.lobbyId)
            partyInvitationFragment.findNavController().navigate(R.id.action_partyInvitationFragment_to_acceptInviteToLobbyLoadingFragment, bundle)
        }

        declineButton.setOnClickListener {
            user.pendingInviteRequests!!.remove(user.pendingInviteRequests!![position])
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, user.pendingInviteRequests!!.size)
            serverHandler.apiCall(
                Config.DELETE,
                Config.DELETE_LOBBY_INVITE,
                userId = user.userId,
                lobbyId = friend.lobbyId
            )
        }
    }

    override fun getItemCount(): Int {
        if (user.pendingInviteRequests != null) return user.pendingInviteRequests!!.size
        return 0
    }
}