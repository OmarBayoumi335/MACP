package com.example.androidstudio.classes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.types.User
import org.json.JSONObject

class InviteFriendListAdapter(private val c: Context,
                              private val mUser: List<User>,
                              private val uid: String,
                              private val serverHandler: ServerHandler,
                              private val lobbyId: String): RecyclerView.Adapter<InviteFriendListAdapter.ViewHolder>(){

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend: User = mUser[position]
        // Set item views based on your views and data model
        val tvUsername = holder.tvUsername
        val tvId = holder.tvId
        val bPositive = holder.bInvite
        tvUsername.text = friend.username
        tvId.text = friend.userId
        bPositive.setOnClickListener {
//            serverHandler.postSendLobbyInvite(uid, friend.getId(), lobbyId, object : ServerHandler.VolleyCallBack {
//                override fun onSuccess(reply: JSONObject?) {
////                    val status = reply?.get("status")
//                    val message = reply?.get("message").toString()
//                    Toast.makeText(c, message, Toast.LENGTH_SHORT).show()
////                    if (status == "invited") {
////                        Toast.makeText(c, message, Toast.LENGTH_SHORT).show()
////                    } else if (status == "alreadyInvited") {
////                        bPositive.visibility = View.GONE
////                    } else if (status == "alreadyinLobby") {
////                        bPositive.visibility = View.GONE
////                    }
//                }
//            })
        }

    }

    override fun getItemCount(): Int {
        return mUser.size
    }
}