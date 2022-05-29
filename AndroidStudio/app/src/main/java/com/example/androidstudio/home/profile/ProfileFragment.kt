package com.example.androidstudio.home.profile

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
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
import com.example.androidstudio.classes.utils.UpdateUI
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONObject


class ProfileFragment : DialogFragment(), View.OnClickListener {

//    private lateinit var profileFriendsRecyclerView: RecyclerView

    // Username
    private lateinit var nameEditText: EditText
    private lateinit var username: String

    // Id
    private lateinit var idTextView: TextView
    private lateinit var userid: String

    // Change name
    private lateinit var changeNameButton: Button
    private lateinit var newName: String

//    private lateinit var rootView: View
//    private lateinit var viewAlert: View

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

        // UID
        sharedPreferences = requireActivity().getSharedPreferences("lastId", MODE_PRIVATE)
        userid = sharedPreferences.getString("ID", "").toString()

        // Set top part views
        nameEditText = rootView.findViewById(R.id.profile_name_edittext)
        idTextView = rootView.findViewById(R.id.profile_id)
        serverHandler = ServerHandler(requireContext())
        serverHandler.getUserInformation(userid, object : ServerHandler.VolleyCallBack {
            override fun onSuccess(reply: JSONObject?) {
                idTextView.text = resources.getString(R.string.id).plus(userid)
                username = reply?.get("username").toString()
                nameEditText.setText(username)
            }
        })

        // Set the profile tab menu
        val pager = rootView.findViewById<ViewPager2>(R.id.profile_view_pager)
        val table = rootView.findViewById<TabLayout>(R.id.profile_table)
        pager.adapter = ProfileViewPageAdapter(this, requireActivity().supportFragmentManager, lifecycle)
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
        UpdateUI.updateNotificationInProfile(this, serverHandler, userid, requestsTextView)

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
            R.id.profile_change_name_button -> changeName()
            R.id.profile_close_button -> dismiss()
        }
    }

    private fun changeName() {
        username = newName
        serverHandler.postChangeName(userid, username)
        changeNameButton.visibility = View.GONE
    }
}