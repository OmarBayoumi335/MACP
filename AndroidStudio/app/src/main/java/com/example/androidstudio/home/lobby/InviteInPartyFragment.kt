package com.example.androidstudio.home.lobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.classes.adapters.InviteFriendListAdapter
import com.example.androidstudio.classes.types.Lobby
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.types.UserIdentification
import com.example.androidstudio.classes.utils.Config
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.json.JSONObject

class InviteInPartyFragment(lobby: Lobby, user: User) : DialogFragment(), View.OnClickListener {

    private val dataBase = FirebaseDatabase.getInstance().reference

    private var lobby: Lobby
    private var user: User

    init {
        this.lobby = lobby
        this.user = user
    }

    private lateinit var serverHandler: ServerHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_invite_in_party, container, false)
        serverHandler = ServerHandler(requireContext())

        val closeButton = rootView.findViewById<Button>(R.id.invite_friend_close_button)

        closeButton.setOnClickListener(this)

//        rootView.findViewById<ProgressBar>(R.id.progressBarInvite).visibility = View.GONE
//        closeButton.visibility = View.VISIBLE
//        rootView.findViewById<TextView>(R.id.invite_friend_title_textview).visibility = View.VISIBLE

        // Recycler view with update every sec
        val inviteFriendListRecyclerView = rootView.findViewById<RecyclerView>(R.id.invite_friend_recyclerview)
        inviteFriendListRecyclerView.visibility = View.VISIBLE
        val inviteFriendListAdapter = InviteFriendListAdapter(user, lobby, serverHandler, requireContext())
        inviteFriendListRecyclerView.adapter = inviteFriendListAdapter
        inviteFriendListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        update(inviteFriendListAdapter)
//        serverHandler.apiCall(
//            Config.GET,
//            Config.GET_INVITABLE_USER,
//            user.userId,
//            lobbyId = lobby.lobbyId,
//            callBack = object: ServerHandler.VolleyCallBack{
//                override fun onSuccess(reply: JSONObject?) {
//                    val userInvitableJsonString = reply?.get("userInvitableList").toString()
//                    val gson = Gson()
//                    userInvitableList = gson.fromJson(
//                        userInvitableJsonString,
//                        UserInvitableList::class.java
//                    )
//                    rootView.findViewById<ProgressBar>(R.id.progressBarInvite).visibility = View.GONE
//                    closeButton.visibility = View.VISIBLE
//                    rootView.findViewById<TextView>(R.id.invite_friend_title_textview).visibility = View.VISIBLE
//
//                    // Recycler view with update every sec
//                    val inviteFriendListRecyclerView = rootView.findViewById<RecyclerView>(R.id.invite_friend_recyclerview)
//                    inviteFriendListRecyclerView.visibility = View.VISIBLE
//                    val inviteFriendListAdapter = InviteFriendListAdapter(user, userInvitableList, lobby, serverHandler, requireContext())
//                    inviteFriendListRecyclerView.adapter = inviteFriendListAdapter
//                    inviteFriendListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//                    update(inviteFriendListAdapter, inviteInPartyFragment)
//                }
//        })
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the fragment dimension to 90% of the device size
        val w = (resources.displayMetrics.widthPixels * 0.80).toInt()
        val h = (resources.displayMetrics.heightPixels * 0.80).toInt()
        val viewResize: View = view.findViewById(R.id.invite_in_party_fragment)
        val layoutParams: ViewGroup.LayoutParams? = viewResize.layoutParams
        layoutParams?.width = w
        layoutParams?.height = h
        viewResize.layoutParams = layoutParams
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.invite_friend_close_button -> dismiss()
        }
    }

    private fun update(
        inviteFriendListAdapter: InviteFriendListAdapter
    ) {
        dataBase.child("Users").child(user.userId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newUser = snapshot.getValue(User::class.java)
                if (newUser != null) {
                    if (newUser.friends != null) {
                        if (newUser.friends != user.friends)
                        user.friends = newUser.friends
                        inviteFriendListAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
//        serverHandler.apiCall(
//            Config.GET,
//            Config.GET_INVITABLE_USER,
//            user.userId,
//            lobbyId = lobby.lobbyId,
//            callBack = object: ServerHandler.VolleyCallBack{
//                override fun onSuccess(reply: JSONObject?) {
//                    val userInvitableJsonString = reply?.get("userInvitableList").toString()
//                    val gson = Gson()
//                    val userInvitableListNew = gson.fromJson(
//                        userInvitableJsonString,
//                        UserInvitableList::class.java
//                    )
//                    userInvitableList.userList = userInvitableListNew.userList
//                    inviteFriendListAdapter.notifyDataSetChanged()
//                    if (inviteInPartyFragment.context != null) {
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            update(inviteFriendListAdapter, inviteInPartyFragment)
//                        },
//                            Config.POLLING_PERIOD)
//                    }
//                }
//            })
    }
}