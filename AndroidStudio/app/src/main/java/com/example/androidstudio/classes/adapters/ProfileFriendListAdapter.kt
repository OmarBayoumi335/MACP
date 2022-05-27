package com.example.androidstudio.classes.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.User

class ProfileFriendListAdapter(private val c: Context,
                               private val mUser: List<User>,
                               private val tab: Int,
                               private val uid: String,
                               private val serverHandler: ServerHandler
): RecyclerView.Adapter<ProfileFriendListAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView = itemView.findViewById(R.id.friend_item_username_textview)
        var tvId: TextView = itemView.findViewById(R.id.friend_item_id_textview)
        var bPositive: ImageButton = itemView.findViewById(R.id.positive_item_image_button)
        var bNegative: ImageButton = itemView.findViewById(R.id.negative_item_image_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item_profile_friend_list, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend: User = mUser[position]
        // Set item views based on your views and data model
        val tvUsername = holder.tvUsername
        val tvId = holder.tvId
        val bPositive = holder.bPositive
        val bNegative = holder.bNegative
        tvUsername.text = friend.getUsername()
        tvId.text = friend.getId()
        if (tab == 0) {
            holder.bPositive.visibility = View.GONE
            bNegative.setOnClickListener {
                AlertDialog.Builder(c)
                    .setTitle(R.string.remove_friend_alert_title)
                    .setMessage(c.resources.getString(R.string.remove_friend_alert_message) + "\n" + friend.getId() + " " + friend.getUsername() + " ?")
                    .setPositiveButton(
                        R.string.yes
                    ) { _, _ ->
                        serverHandler.deleteFriend(uid, friend.getId())
                    }
                    .setNegativeButton(R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        } else if (tab == 1) {
            bPositive.setOnClickListener {
                serverHandler.postAcceptFriendRequest(uid, friend.getId())
            }
            bNegative.setOnClickListener {
                serverHandler.deleteRejectFriendRequest(uid, friend.getId())
            }
        }
    }

    override fun getItemCount(): Int {
        return mUser.size
    }
}