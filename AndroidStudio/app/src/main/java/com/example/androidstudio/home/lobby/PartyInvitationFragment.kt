package com.example.androidstudio.home.lobby

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.classes.adapters.LobbyInvitesAdapter
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.home.MenuActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PartyInvitationFragment : Fragment(), View.OnClickListener {

    private val dataBase = FirebaseDatabase.getInstance().reference
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_party_invitation, container, false)
        val serverHandler = ServerHandler(requireContext())

        // user
        val menuActivity: MenuActivity = requireActivity() as MenuActivity
        user = menuActivity.getUser()

        // Back button
        val backButton = rootView.findViewById<Button>(R.id.button_back_from_party_invitation)
        backButton.setOnClickListener(this)

        // Recycler view for invites with update every sec
        val lobbyInviteRecyclerView = rootView.findViewById<RecyclerView>(R.id.party_invites_recycler_view)
//        UpdateUI.updateInviteList(requireContext(), this, serverHandler, userid, lobbyInviteAdapter)
        val lobbyInvitesAdapter = LobbyInvitesAdapter(user, this, serverHandler)
        lobbyInviteRecyclerView.adapter = lobbyInvitesAdapter
        lobbyInviteRecyclerView.layoutManager = LinearLayoutManager(context)

        update(lobbyInvitesAdapter)

        return rootView
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_back_from_party_invitation -> back()
        }
    }

    private fun back() {
        findNavController().popBackStack()
    }

    private fun update(lobbyInvitesAdapter: LobbyInvitesAdapter) {
        dataBase.child("Users").child(user.userId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lobbyInvitesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
//        if (this.context != null) {
//            Handler(Looper.getMainLooper()).postDelayed({
//                update(lobbyInvitesAdapter)
//            },
//                Config.POLLING_PERIOD)
//        }
    }
}