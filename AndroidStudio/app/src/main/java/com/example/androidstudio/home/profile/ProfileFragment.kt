package com.example.androidstudio.home.profile

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.androidstudio.R
import com.example.androidstudio.classi.*
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_profile.*
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
        sharedPreferences = requireActivity().getSharedPreferences("lastGoogleId", MODE_PRIVATE)
        userid = sharedPreferences.getString("UID", "").toString()

        // Set top part views
        nameEditText = rootView.findViewById(R.id.profile_name_edittext)
        idTextView = rootView.findViewById(R.id.profile_id)
        serverHandler = ServerHandler(requireContext())
        serverHandler.getUserInformation(userid, object : ServerHandler.VolleyCallBack {
            override fun onSuccess(reply: JSONObject?) {
                idTextView.text = resources.getString(R.string.id).plus(reply?.get("id").toString())
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


//        val addFriendButton = view.findViewById<ImageButton>(R.id.profile_add_friend_button)
//        addFriendButton.setOnClickListener(this)

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
//            R.id.profile_add_friend_button -> addFriendMenu()
//            R.id.add_friend_button -> addFriend()
        }
    }

    private fun changeName() {
        username = newName
        serverHandler.postChangeName(userid, username)
        changeNameButton.visibility = View.GONE
    }

//    private fun addFriendMenu() {
//        val inflater = LayoutInflater.from(context)
//        viewAlert = inflater.inflate(R.layout.add_friend_alert, null)
//        val alertDialog = AlertDialog.Builder(context).setView(viewAlert).create()
//        val addFriendButton = viewAlert.findViewById<Button>(R.id.add_friend_button)
//        addFriendButton.setOnClickListener(this)
//        val addFriendCloseButton = viewAlert.findViewById<Button>(R.id.add_friend_close_button)
//        addFriendCloseButton.setOnClickListener{
//            alertDialog.dismiss()
//        }
//        alertDialog.show()
//    }


//    private fun addFriend() {
//        val findFriendName = viewAlert.findViewById<EditText>(R.id.find_friend_editText)
//        serverHandler.getSearchFriend(userid, findFriendName.text.toString(), object : ServerHandler.VolleyCallBack {
//            override fun onSuccess(reply: JSONObject?) {
//                val searchResult = viewAlert.findViewById<TextView>(R.id.add_friend_result_textview)
//                val found = reply?.get("status").toString()
//                if (found == "found"){
//                    Log.i(Config.ADDFRIENDTAG, "User found")
//                    searchResult.text = resources.getString(R.string.friend_found)
//                    searchResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.friend_found))
//                    serverHandler.postSendFriendRequest(userid, findFriendName.text.toString())
//                }
//                if (found == "alreadySent"){
//                    Log.i(Config.ADDFRIENDTAG, "Request at this user already sent")
//                    searchResult.text = resources.getString(R.string.friend_already_sent)
//                    searchResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.friend_sent_added))
//                }
//                if (found == "alreadyAdded"){
//                    Log.i(Config.ADDFRIENDTAG, "User already added")
//                    searchResult.text = resources.getString(R.string.friend_already_added)
//                    searchResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.friend_sent_added))
//                }
//                if (found == "notFound"){
//                    Log.i(Config.ADDFRIENDTAG, "User not found")
//                    searchResult.text = resources.getString(R.string.friend_not_found)
//                    searchResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.friend_not_found))
//                }
//            }
//        })
//
//    }
}