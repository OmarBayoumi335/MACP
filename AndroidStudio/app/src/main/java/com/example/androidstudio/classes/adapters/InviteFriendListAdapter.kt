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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.classes.types.*
import com.example.androidstudio.classes.utils.Config
import org.json.JSONObject

class InviteFriendListAdapter(user: User,
                              lobby: Lobby,
                              private val serverHandler: ServerHandler,
                              private val c: Context): RecyclerView.Adapter<InviteFriendListAdapter.ViewHolder>(){

    private var user: User
    private var lobby: Lobby

    init {
        this.user = user
        this.lobby = lobby
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView = itemView.findViewById(R.id.invite_friend_item_username_textview)
        var tvId: TextView = itemView.findViewById(R.id.invite_friend_item_id_textview)
        var bInvite: ImageButton = itemView.findViewById(R.id.invite_item_image_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item_invite_friend_list, parent, false)
        return ViewHolder(contactView)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend: UserIdentification = user.friends!![position]
        // Set item views based on your views and data model
        val tvUsername = holder.tvUsername
        val tvId = holder.tvId
        val bPositive = holder.bInvite
        tvUsername.text = friend.username
        tvId.text = friend.userId
        if (friend.invitable) {
            bPositive.visibility = View.VISIBLE
            bPositive.setOnTouchListener { v, event ->
                val scaleUp = AnimationUtils.loadAnimation(c, R.anim.scale_up)
                val scaleDown = AnimationUtils.loadAnimation(c, R.anim.scale_down)
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> v?.startAnimation(scaleDown)
                    MotionEvent.ACTION_UP -> {
                        v?.startAnimation(scaleUp)
                        if ((lobby.team1 + lobby.team2).size == Config.MAX_TEAM_MEMBERS*2) {
                            val message = c.resources.getString(R.string.lobby_full)
                            Toast.makeText(c, message, Toast.LENGTH_SHORT).show()
                        } else {
                            bPositive.visibility = View.GONE
                            user.friends!![position].invitable = false
                            notifyDataSetChanged()
                            serverHandler.apiCall(
                                Config.POST,
                                Config.POST_SEND_LOBBY_INVITE,
                                userId = user.userId,
                                username = user.username,
                                friendId = friend.userId,
                                lobbyId = lobby.lobbyId,
                                lobbyName = lobby.lobbyName,
                                callBack = object : ServerHandler.VolleyCallBack {
                                    override fun onSuccess(reply: JSONObject?) {
                                        val message = c.resources.getString(R.string.user_invited)
                                        Toast.makeText(c, message, Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }

                    }
                }
                v?.onTouchEvent(event) ?: true
            }
        } else {
            bPositive.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return user.friends!!.size
    }
}