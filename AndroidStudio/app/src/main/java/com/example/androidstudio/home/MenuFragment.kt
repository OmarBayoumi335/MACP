package com.example.androidstudio.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidstudio.Login.LoginActivity
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class MenuFragment: Fragment(), View.OnTouchListener {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var clientId = Config.CLIENT_ID
    private lateinit var user: User
    private lateinit var playButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

        Log.i(Config.LOGIN_TAG, resources.displayMetrics.densityDpi.toString())
        val menuActivity: MenuActivity = requireActivity() as MenuActivity
        user = menuActivity.getUser()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        logoutButton.setOnTouchListener(this)

        playButton = view.findViewById<Button>(R.id.button_play)
        playButton.setOnTouchListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, motionEvent: MotionEvent?): Boolean {
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        when (motionEvent?.action) {
            MotionEvent.ACTION_DOWN -> v?.startAnimation(scaleDown)
            MotionEvent.ACTION_UP -> {
                v?.startAnimation(scaleUp)
                when(v?.id) {
                    R.id.logout_button  -> signOut()
                    R.id.button_play    -> openSetupGame()
                }
            }
        }
        return true
    }

    private fun openSetupGame() {
        findNavController().navigate(R.id.action_menuFragment_to_setupGameFragment)
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity()) {
                Log.i(Config.LOGIN_TAG, "Logged out")
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
    }
}