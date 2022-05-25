package com.example.androidstudio.home.profile

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidstudio.R
import com.example.androidstudio.classi.Config
import com.example.androidstudio.classi.ProfileFriendListAdapter
import com.example.androidstudio.classi.ServerHandler
import com.example.androidstudio.classi.User
import org.json.JSONObject
import kotlin.reflect.typeOf


class ProfileFragment : DialogFragment(), View.OnClickListener {

    private lateinit var nameEditText: EditText
    private lateinit var idTextView: TextView

    private lateinit var profileFriendsRecyclerView: RecyclerView
    private lateinit var profileFriendListAdapter: ProfileFriendListAdapter

    private lateinit var username: String
    private lateinit var newName: String
    private lateinit var changeNameButton: Button

    private lateinit var rootView: View
    private lateinit var viewAlert: View

    private lateinit var serverHandler: ServerHandler
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("lastGoogleId", MODE_PRIVATE)
        userid = sharedPreferences.getString("UID", "").toString()

        nameEditText = rootView.findViewById(R.id.profile_name_edittext)
        idTextView = rootView.findViewById(R.id.profile_id)

        profileFriendsRecyclerView = rootView.findViewById(R.id.profile_friends_list_recyclerView)

        serverHandler = ServerHandler(requireContext())
        serverHandler.getUserInformation(userid, object : ServerHandler.VolleyCallBack {
            override fun onSuccess(reply: JSONObject?) {
                idTextView.text = reply?.get("id").toString()
                username = reply?.get("username").toString()
                nameEditText.setText(username)
            }
        })

        serverHandler.getFriendsList(userid, object : ServerHandler.VolleyCallBack {
            override fun onSuccess(reply: JSONObject?) {
                val friendList = reply
                serverHandler.getPendingFriendsRequest(userid, object : ServerHandler.VolleyCallBack {
                    override fun onSuccess(reply: JSONObject?) {
                        val pendingList = reply
                        var friendListView: List<User> = emptyList()

                        if (pendingList!!.get("pendingFriendRequests").toString() != "null") {
                            val pendingRequests = pendingList.getJSONArray("pendingFriendRequests")
                            val pendingRequestsNumber = pendingRequests.length()
                            for (i in 0 until pendingRequestsNumber) {
                                val id = pendingRequests[i].toString().substring(7, 15)
                                val username = pendingRequests[i].toString().substring(29, pendingRequests[i].toString().length - 2)
                                val pendingUser = User(username, id)
                                friendListView += pendingUser
                            }
                        }

                        if (friendList!!.get("friends").toString() != "null") {
                            val friends = friendList.getJSONArray("friends")
                            val friendsNumber = friends.length()
                            for (i in 0 until friendsNumber) {
                                val id = friends[i].toString().substring(7, 15)
                                val username = friends[i].toString()
                                    .substring(29, friends[i].toString().length - 2)
                                val friendUser = User(username, id)
                                friendListView += friendUser
                            }
                        }
                        Log.i(Config.API, "" + friendListView.toString())
                        profileFriendListAdapter = ProfileFriendListAdapter(friendListView)
                        profileFriendsRecyclerView.adapter = profileFriendListAdapter
                        profileFriendsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    }
                })
            }
        })

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addFriendButton = view.findViewById<ImageButton>(R.id.profile_add_friend_button)
        addFriendButton.setOnClickListener(this)

        val profileCloseButton = view.findViewById<Button>(R.id.profile_close_button)
        profileCloseButton.setOnClickListener(this)

        changeNameButton = view.findViewById<Button>(R.id.profile_change_name_button)
        changeNameButton.visibility = View.GONE
        changeNameButton.setOnClickListener(this)

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                newName = s.toString()
                if (s.toString() == username) {
                    changeNameButton.visibility = View.GONE
                }
                else {
                    changeNameButton.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile_add_friend_button -> addFriendMenu()
            R.id.profile_change_name_button -> changeName()
            R.id.add_friend_button -> addFriend()
            R.id.profile_close_button -> dismiss()
        }
    }

    private fun addFriendMenu() {
        val inflater = LayoutInflater.from(context)
        viewAlert = inflater.inflate(R.layout.add_friend_alert, null)
        val alertDialog = AlertDialog.Builder(context).setView(viewAlert).create()
        val addFriendButton = viewAlert.findViewById<Button>(R.id.add_friend_button)
        addFriendButton.setOnClickListener(this)
        val addFriendCloseButton = viewAlert.findViewById<Button>(R.id.add_friend_close_button)
        addFriendCloseButton.setOnClickListener{
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun changeName() {
        username = newName
        serverHandler.postChangeName(userid, username)
        changeNameButton.visibility = View.GONE
    }

    private fun addFriend() {
        val findFriendName = viewAlert.findViewById<EditText>(R.id.find_friend_editText)
        serverHandler.getSearchFriend(userid, findFriendName.text.toString(), object : ServerHandler.VolleyCallBack {
            override fun onSuccess(reply: JSONObject?) {
                val searchResult = viewAlert.findViewById<TextView>(R.id.add_friend_result_textview)
                val found = reply?.get("status").toString()
                if (found == "found"){
                    Log.i(Config.ADDFRIENDTAG, "User found")
                    searchResult.text = resources.getString(R.string.friend_found)
                    searchResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.friend_found))
                    serverHandler.postSendFriendRequest(userid, findFriendName.text.toString())
                }
                if (found == "alreadySent"){
                    Log.i(Config.ADDFRIENDTAG, "Request at this user already sent")
                    searchResult.text = resources.getString(R.string.friend_already_sent)
                    searchResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.friend_sent_added))
                }
                if (found == "alreadyAdded"){
                    Log.i(Config.ADDFRIENDTAG, "User already added")
                    searchResult.text = resources.getString(R.string.friend_already_added)
                    searchResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.friend_sent_added))
                }
                if (found == "notFound"){
                    Log.i(Config.ADDFRIENDTAG, "User not found")
                    searchResult.text = resources.getString(R.string.friend_not_found)
                    searchResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.friend_not_found))
                }
            }
        })

    }
}