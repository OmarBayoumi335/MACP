package com.example.androidstudio.home.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.androidstudio.R
import com.example.androidstudio.classi.Config
import com.example.androidstudio.classi.ServerHandler
import org.json.JSONObject

class AddFriendFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_friend, container, false)

        // For server calls
        val serverHandler = ServerHandler(requireContext())

        // UID
        val sharedPreferences = requireActivity().getSharedPreferences("lastGoogleId", Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("UID", "").toString()

        // Friend to search
        val findFriendName = rootView.findViewById<EditText>(R.id.find_friend_editText)

        // Add friend button
        val addFriendButton = rootView.findViewById<Button>(R.id.add_friend_button)
        addFriendButton.setOnClickListener{
            addFriend(rootView, userid, findFriendName, serverHandler)
        }
        return rootView
    }

    private fun addFriend(rootView: View, userid: String, findFriendName: EditText, serverHandler: ServerHandler) {
        var toFindFriendName = findFriendName.text.toString()
        if (toFindFriendName == "") {
            toFindFriendName = " "
        }
        serverHandler.getSearchFriend(userid, toFindFriendName, object : ServerHandler.VolleyCallBack {
            override fun onSuccess(reply: JSONObject?) {
                val searchResult = rootView.findViewById<TextView>(R.id.add_friend_result_textview)
                val found = reply?.get("status").toString()
                if (found == "found"){
                    Log.i(Config.ADDFRIENDTAG, "User found")
                    searchResult.text = resources.getString(R.string.friend_found)
                    searchResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.friend_found))
                    serverHandler.postSendFriendRequest(userid, toFindFriendName)
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