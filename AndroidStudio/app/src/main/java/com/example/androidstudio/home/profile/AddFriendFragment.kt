package com.example.androidstudio.home.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.androidstudio.R
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import org.json.JSONObject

class AddFriendFragment(user: User) : Fragment(), View.OnTouchListener {

    private val user: User
    private lateinit var findFriendName: EditText
    private lateinit var serverHandler: ServerHandler
    private lateinit var rootView: View

    init {
        this.user = user
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_add_friend, container, false)

        // For server calls
        serverHandler = ServerHandler(requireContext())

        // Friend to search
        findFriendName = rootView.findViewById<EditText>(R.id.find_friend_editText)

        // Add friend button
        val addFriendButton = rootView.findViewById<Button>(R.id.add_friend_button)
        addFriendButton.setOnTouchListener(this)
        return rootView
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, motionEvent: MotionEvent?): Boolean {
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        when (motionEvent?.action) {
            MotionEvent.ACTION_DOWN -> v?.startAnimation(scaleDown)
            MotionEvent.ACTION_UP -> {
                v?.startAnimation(scaleUp)
                when (v?.id) {
                    R.id.add_friend_button -> addFriend(rootView, findFriendName, serverHandler)
                }
            }
        }
        return true
    }

    private fun addFriend(rootView: View, findFriendName: EditText, serverHandler: ServerHandler) {
        var toFindFriendId = findFriendName.text.toString()
        if (toFindFriendId == "") {
            toFindFriendId = " "
        }
        serverHandler.apiCall(
            Config.GET,
            Config.GET_SEARCH_FRIEND,
            userId = user.userId,
            friendId = toFindFriendId,
            callBack = object : ServerHandler.VolleyCallBack {
                override fun onSuccess(reply: JSONObject?) {
                    val searchResult =
                        rootView.findViewById<TextView>(R.id.add_friend_result_textview)
                    val found = reply?.get("status").toString()
                    Log.i(Config.ADD_FRIEND_TAG, reply?.get("message").toString())
                    if (found == "found") {
                        searchResult.text = resources.getString(R.string.friend_found)
                        searchResult.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.friend_found
                            )
                        )
                        serverHandler.apiCall(
                            Config.POST,
                            Config.POST_SEND_FRIEND_REQUEST,
                            userId = user.userId,
                            friendId = toFindFriendId
                        )
                    }
                    if (found == "alreadySent") {
                        searchResult.text = resources.getString(R.string.friend_already_sent)
                        searchResult.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.friend_sent_added
                            )
                        )
                    }
                    if (found == "alreadyAdded") {
                        searchResult.text = resources.getString(R.string.friend_already_added)
                        searchResult.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.friend_sent_added
                            )
                        )
                    }
                    if (found == "inPending") {
                        searchResult.text = resources.getString(R.string.friend_in_pending)
                        searchResult.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.friend_sent_added
                            )
                        )
                    }
                    if (found == "yourself") {
                        searchResult.text = resources.getString(R.string.friend_yourself)
                        searchResult.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.friend_sent_added
                            )
                        )
                    }
                    if (found == "notFound") {
                        searchResult.text = resources.getString(R.string.friend_not_found)
                        searchResult.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.friend_not_found
                            )
                        )
                    }
                }
            })
    }
}