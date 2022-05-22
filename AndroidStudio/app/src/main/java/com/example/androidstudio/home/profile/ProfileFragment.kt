package com.example.androidstudio.home.profile

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.androidstudio.R


class ProfileFragment : DialogFragment(), View.OnClickListener {

    private var tmp: String = "Cristiano"
    private lateinit var newName: String
    private lateinit var changeNameButton: Button

    private lateinit var rootView: View
    private lateinit var viewAlert: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addFriendButton = rootView.findViewById<ImageButton>(R.id.profile_add_friend_button)
        addFriendButton.setOnClickListener(this)

        val profileCloseButton = rootView.findViewById<Button>(R.id.profile_close_button)
        profileCloseButton.setOnClickListener(this)

        val nameEditText = rootView.findViewById<EditText>(R.id.profile_name_edittext)
        nameEditText.setText(tmp)

        changeNameButton = rootView.findViewById<Button>(R.id.profile_change_name_button)
        changeNameButton.visibility = View.GONE
        changeNameButton.setOnClickListener(this)

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                newName = s.toString()
                if (s.toString() == tmp) {
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
        tmp = newName
        changeNameButton.visibility = View.GONE
    }

    private fun addFriend() {
        val findFriendName = viewAlert.findViewById<EditText>(R.id.find_friend_editText)
        val searchResult = viewAlert.findViewById<TextView>(R.id.add_friend_result_textview)

        searchResult.text = "Trovato"
        searchResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.friend_not_found))
    }
}