package com.example.androidstudio.home.profile

import android.app.AlertDialog
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
import androidx.fragment.app.DialogFragment
import com.example.androidstudio.R


class ProfileFragment : DialogFragment(), View.OnClickListener {

    private var tmp: String = "Cristiano"
    private lateinit var newName: String
    private lateinit var changeNameButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        val addFriendButton = rootView.findViewById<ImageButton>(R.id.profile_add_friend_button)
        addFriendButton.setOnClickListener(this)

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
                    Log.i("Profile", "Equal" + s.toString())
                }
                else {
                    changeNameButton.visibility = View.VISIBLE
                    Log.i("Profile", "Different" + s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        return rootView
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile_add_friend_button -> addFriend()
            R.id.profile_change_name_button -> changeName()
        }
    }

    private fun changeName() {
        tmp = newName
        changeNameButton.visibility = View.GONE
    }

    private fun addFriend() {
//        AddFriendFragment().show(parentFragmentManager, "qqqqqq")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.add_friend_alert, null)
        val alertDialog: AlertDialog = AlertDialog.Builder(context).setView(view).create()
        alertDialog.show()
    }

}