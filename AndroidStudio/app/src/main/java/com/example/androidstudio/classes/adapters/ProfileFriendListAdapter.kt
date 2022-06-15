package com.example.androidstudio.classes.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.types.UserIdentification
import com.example.androidstudio.classes.utils.Config

class ProfileFriendListAdapter(private val c: Context,
                               user: User,
                               private val tab: Int,
                               private val serverHandler: ServerHandler,
                               private val notificationProfile: TextView? = null,
                               private val notificationInProfile: TextView? = null
): RecyclerView.Adapter<ProfileFriendListAdapter.ViewHolder>(){

    private var user: User

    init {
        this.user = user
    }

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
        if (tab == 0) {
            tabFriends(holder, position)
        } else if (tab == 1) {
            tabRequests(holder, position)
        }
    }

    override fun getItemCount(): Int {
        if (tab == 0 && user.friends != null) return user.friends!!.size
        if (tab == 1 && user.pendingFriendRequests != null) return user.pendingFriendRequests!!.size
        return 0
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun tabFriends(holder: ViewHolder, position: Int) {
        val friend: UserIdentification = user.friends!![position]
        val tvUsername = holder.tvUsername
        val tvId = holder.tvId
        val deleteButton = holder.bNegative
        tvUsername.text = friend.username
        tvId.text = friend.userId
        holder.bPositive.visibility = View.GONE
        deleteButton.setOnTouchListener { v, event ->
            val scaleUp = AnimationUtils.loadAnimation(c, R.anim.scale_up)
            val scaleDown = AnimationUtils.loadAnimation(c, R.anim.scale_down)
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> v?.startAnimation(scaleDown)
                MotionEvent.ACTION_UP -> {
                    v?.startAnimation(scaleUp)
                    AlertDialog.Builder(c, R.style.BackgroundDialog)
                        .setTitle(R.string.remove_friend_alert_title)
                        .setMessage(c.resources.getString(R.string.remove_friend_alert_message) + "\n" + friend.userId + " " + friend.username + " ?")
                        .setPositiveButton(
                            R.string.yes
                        ) { _, _ ->
                            user.friends!!.remove(user.friends!![position]);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, user.friends!!.size);
                            serverHandler.apiCall(
                                Config.DELETE,
                                Config.DELETE_REMOVE_FRIEND,
                                friendId = friend.userId,
                                userId = user.userId
                            )
                        }
                        .setNegativeButton(R.string.no, null)
                        .setIcon(R.drawable.alert)
                        .show()
                        .findViewById<TextView>(android.R.id.message)
                        .setTypeface(Typeface.createFromAsset(c.assets, "booster_next_fy_black.ttf"))
                }
            }
            v?.onTouchEvent(event) ?: true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun tabRequests(holder: ViewHolder, position: Int) {
        val request: UserIdentification = user.pendingFriendRequests!![position]
        val tvUsername = holder.tvUsername
        val tvId = holder.tvId
        val acceptButton = holder.bPositive
        val rejectButton = holder.bNegative
        tvUsername.text = request.username
        tvId.text = request.userId
        acceptButton.setOnTouchListener { v, event ->
            val scaleUp = AnimationUtils.loadAnimation(c, R.anim.scale_up)
            val scaleDown = AnimationUtils.loadAnimation(c, R.anim.scale_down)
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> v?.startAnimation(scaleDown)
                MotionEvent.ACTION_UP -> {
                    v?.startAnimation(scaleUp)
                    user.pendingFriendRequests!!.remove(user.pendingFriendRequests!![position])
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, user.pendingFriendRequests!!.size)
                    if (user.pendingFriendRequests == null || user.pendingFriendRequests?.size == 0){
                        notificationProfile?.visibility = View.GONE
                        notificationInProfile?.visibility = View.GONE
                    } else {
                        notificationProfile?.visibility = View.VISIBLE
                        notificationInProfile?.visibility = View.VISIBLE
                        notificationProfile?.text = user.pendingFriendRequests!!.size.toString()
                        notificationInProfile?.text = user.pendingFriendRequests!!.size.toString()
                    }
                    serverHandler.apiCall(
                        Config.POST,
                        Config.POST_ACCEPT_FRIEND_REQUEST,
                        userId = user.userId,
                        friendId = request.userId
                    )
                }
            }
            v?.onTouchEvent(event) ?: true
        }
        rejectButton.setOnTouchListener { v, event ->
            val scaleUp = AnimationUtils.loadAnimation(c, R.anim.scale_up)
            val scaleDown = AnimationUtils.loadAnimation(c, R.anim.scale_down)
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> v?.startAnimation(scaleDown)
                MotionEvent.ACTION_UP -> {
                    v?.startAnimation(scaleUp)
                    user.pendingFriendRequests!!.remove(user.pendingFriendRequests!![position])
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, user.pendingFriendRequests!!.size)
                    if (user.pendingFriendRequests == null || user.pendingFriendRequests?.size == 0){
                        notificationProfile?.visibility = View.GONE
                        notificationInProfile?.visibility = View.GONE
                    } else {
                        notificationProfile?.visibility = View.VISIBLE
                        notificationInProfile?.visibility = View.VISIBLE
                        notificationProfile?.text = user.pendingFriendRequests!!.size.toString()
                        notificationInProfile?.text = user.pendingFriendRequests!!.size.toString()
                    }
                    serverHandler.apiCall(
                        Config.DELETE,
                        Config.DELETE_REMOVE_FRIEND_REQUEST,
                        userId = user.userId,
                        friendId = request.userId
                    )
                }
            }
            v?.onTouchEvent(event) ?: true
        }
    }
}