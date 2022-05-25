package com.example.androidstudio.classi

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R

class ProfileFriendListAdapter(private val mUser: List<User>): RecyclerView.Adapter<ProfileFriendListAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv1: TextView = itemView.findViewById<TextView>(R.id.textView1)
        var tv2: TextView = itemView.findViewById<TextView>(R.id.textView2)
        var b1: Button = itemView.findViewById<Button>(R.id.button1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.profile_friend_list_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend: User = mUser[position]
        // Set item views based on your views and data model
        val tv1 = holder.tv1
        val tv2 = holder.tv2
        val b1 = holder.b1
        tv1.text = friend.getUsername()
        tv2.text = friend.getId()
        b1.setOnClickListener {
            Log.i(Config.PROFILEFRIENDITEMTAG, "CIAO")
        }
    }

    override fun getItemCount(): Int {
        return mUser.size
    }
}