package com.example.androidstudio.classes.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.classes.types.UserInvite
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.home.lobby.PartyInvitationFragment
import kotlin.coroutines.coroutineContext

class LobbyInvitesAdapter(
    user: User,
    private val partyInvitationFragment: PartyInvitationFragment,
    private val serverHandler: ServerHandler,
    private val context: Context
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

    @SuppressLint("ClickableViewAccessibility")
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

        acceptButton.setOnTouchListener { v, event ->
            val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)
            val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> v?.startAnimation(scaleDown)
                MotionEvent.ACTION_UP -> {
                    v?.startAnimation(scaleUp)
                    user.pendingInviteRequests!!.remove(user.pendingInviteRequests!![position])
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, user.pendingInviteRequests!!.size)
                    val bundle = bundleOf("lobbyId" to friend.lobbyId)
                    partyInvitationFragment.findNavController().navigate(
                        R.id.action_partyInvitationFragment_to_acceptInviteToLobbyLoadingFragment,
                        bundle
                    )
                }
            }
            v?.onTouchEvent(event) ?: true
        }

        declineButton.setOnTouchListener { v, event ->
            val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)
            val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> v?.startAnimation(scaleDown)
                MotionEvent.ACTION_UP -> {
                    v?.startAnimation(scaleUp)
                    declineRequests(friend.lobbyId)
                    serverHandler.apiCall(
                        Config.DELETE,
                        Config.DELETE_LOBBY_INVITE,
                        userId = user.userId,
                        lobbyId = friend.lobbyId
                    )
                }
            }
            v?.onTouchEvent(event) ?: true
        }
    }

    private fun declineRequests(lobbyId: String) {
        for (i in 0 until itemCount) {
            if (user.pendingInviteRequests!![i].lobbyId == lobbyId) {
                user.pendingInviteRequests!!.remove(user.pendingInviteRequests!![i])
                notifyItemRemoved(i)
                notifyItemRangeChanged(i, user.pendingInviteRequests!!.size)
                declineRequests(lobbyId)
                return
            }
        }
    }

    override fun getItemCount(): Int {
        if (user.pendingInviteRequests != null) return user.pendingInviteRequests!!.size
        return 0
    }
}