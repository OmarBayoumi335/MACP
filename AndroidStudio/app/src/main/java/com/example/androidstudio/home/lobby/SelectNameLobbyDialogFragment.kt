package com.example.androidstudio.home.lobby

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.User

class SelectNameLobbyDialogFragment(private val user: User) : DialogFragment(), View.OnTouchListener {

    private lateinit var lobbyName: String
    private lateinit var selectNameEditText: EditText

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_select_name_lobby_dialog, container, false)

        lobbyName = resources.getString(R.string.default_lobby_name)

        selectNameEditText = rootView.findViewById(R.id.select_lobby_name_edittext)

        val cancelButton = rootView.findViewById<Button>(R.id.select_lobby_name_cancel_button)
        cancelButton.setOnTouchListener(this)

        val createLobbyButton = rootView.findViewById<Button>(R.id.select_lobby_name_confirm_button)
        createLobbyButton.setOnTouchListener(this)


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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, motionEvent: MotionEvent?): Boolean {
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        when (motionEvent?.action) {
            MotionEvent.ACTION_DOWN -> v?.startAnimation(scaleUp)
            MotionEvent.ACTION_UP -> {
                v?.startAnimation(scaleDown)
                when (v?.id) {
                    R.id.select_lobby_name_cancel_button -> dismiss()
                    R.id.select_lobby_name_confirm_button -> createLobby()
                }
            }
        }
        return true
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