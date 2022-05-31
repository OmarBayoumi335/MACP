package com.example.androidstudio.home.lobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.User

class SelectNameLobbyDialogFragment(private val user: User) : DialogFragment(), View.OnClickListener {

    private lateinit var lobbyName: String
    private lateinit var selectNameEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_select_name_lobby_dialog, container, false)

        lobbyName = resources.getString(R.string.default_lobby_name)

        selectNameEditText = rootView.findViewById(R.id.select_lobby_name_edittext)

        val cancelButton = rootView.findViewById<Button>(R.id.select_lobby_name_cancel_button)
        cancelButton.setOnClickListener(this)

        val createLobbyButton = rootView.findViewById<Button>(R.id.select_lobby_name_confirm_button)
        createLobbyButton.setOnClickListener(this)


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the fragment dimension to 90% of the device size
        val w = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val h = (resources.displayMetrics.heightPixels * 0.40).toInt()
        val viewResize: View = view.findViewById(R.id.select_name_lobby_dialog)
        val layoutParams: ViewGroup.LayoutParams? = viewResize.layoutParams
        layoutParams?.width = w
        layoutParams?.height = h
        viewResize.layoutParams = layoutParams
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.select_lobby_name_cancel_button -> dismiss()
            R.id.select_lobby_name_confirm_button -> createLobby()
        }
    }

    private fun createLobby() {
        if (selectNameEditText.text.toString() != "") {
            lobbyName = selectNameEditText.text.toString()
        }
        val bundle = bundleOf("lobbyName" to lobbyName)
        findNavController().navigate(R.id.action_setupGameFragment_to_partyCreationLoadingFragment, bundle)
        dismiss()
    }
}