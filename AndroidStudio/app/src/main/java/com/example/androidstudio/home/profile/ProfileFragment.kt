package com.example.androidstudio.home.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.androidstudio.R
import com.example.androidstudio.classes.*
import com.example.androidstudio.classes.adapters.ProfileViewPageAdapter
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ProfileFragment(user: User) : DialogFragment(), View.OnClickListener {

    private val user: User

    init {
        this.user = user
    }

    // Username
    private lateinit var nameEditText: EditText

    // Id
    private lateinit var idTextView: TextView

    // Change name
    private lateinit var changeNameButton: Button
    private lateinit var newName: String

    // Utils
    private lateinit var serverHandler: ServerHandler
    private lateinit var sharedPreferences: SharedPreferences


    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        serverHandler = ServerHandler(requireContext())

        // Set top part views
        nameEditText = rootView.findViewById(R.id.profile_name_edittext)
        idTextView = rootView.findViewById(R.id.profile_id)
        idTextView.text = resources.getString(R.string.id).plus(user.userId)
        nameEditText.setText(user.username)

        // Set the profile tab menu
        val pager = rootView.findViewById<ViewPager2>(R.id.profile_view_pager)
        val table = rootView.findViewById<TabLayout>(R.id.profile_table)
        pager.adapter = ProfileViewPageAdapter(this, requireActivity().supportFragmentManager, lifecycle, user)
        val tabTitles = arrayOf(resources.getString(R.string.profile_friends_tab),
            resources.getString(R.string.profile_friends_request_tab),
            resources.getString(R.string.profile_add_friend_tab))
        val tabIcons: Array<Int> = arrayOf(R.drawable.ic_profile,
            android.R.drawable.checkbox_on_background,
            R.drawable.ic_add_friend)
        TabLayoutMediator(table, pager) {
            tab, position ->
                tab.text = tabTitles[position]
                tab.setIcon(tabIcons[position])
        }.attach()

        // Close profile button
        val profileCloseButton = rootView.findViewById<Button>(R.id.profile_close_button)
        profileCloseButton.setOnClickListener(this)

        // Number of requests
        val requestsTextView = rootView.findViewById<TextView>(R.id.profile_friend_request_notification_textView)
        if (user.pendingFriendRequests != null) {
            requestsTextView.visibility = View.VISIBLE
            requestsTextView.text = user.pendingFriendRequests!!.size.toString()
        }
        update(requestsTextView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the fragment dimension to 90% of the device size
        val w = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val h = (resources.displayMetrics.heightPixels * 0.90).toInt()
        val viewResize: View = view.findViewById(R.id.profile_fragment)
        val layoutParams: ViewGroup.LayoutParams? = viewResize.layoutParams
        layoutParams?.width = w
        layoutParams?.height = h
        viewResize.layoutParams = layoutParams

        // Change name logic
        changeNameButton = view.findViewById(R.id.profile_change_name_button)
        changeNameButton.visibility = View.GONE
        changeNameButton.setOnClickListener(this)
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                newName = s.toString()
                if (newName == user.username) {
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
            R.id.profile_change_name_button -> changeName()
            R.id.profile_close_button -> dismiss()
        }
    }

    private fun changeName() {
        user.username = newName
        serverHandler.apiCall(
            Config.POST,
            Config.POST_CHANGE_NAME,
            userId = user.userId,
            newName = user.username)
        changeNameButton.visibility = View.GONE
    }

    private fun update(notification: TextView) {
        if (user.pendingFriendRequests != null) {
            notification.visibility = View.VISIBLE
            notification.text = user.pendingFriendRequests?.size.toString()
        } else {
            notification.visibility = View.GONE
        }
        notification.text = user.pendingFriendRequests?.size.toString()
        if (this.context != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                update(notification)
            },
                Config.POLLING_PERIOD)
        }
    }
}