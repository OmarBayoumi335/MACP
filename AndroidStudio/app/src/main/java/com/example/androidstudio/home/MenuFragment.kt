package com.example.androidstudio.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidstudio.Login.LoginActivity
import com.example.androidstudio.R
import com.example.androidstudio.classes.utils.Config
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class MenuFragment : Fragment(), View.OnClickListener {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var clientId = Config.CLIENT_ID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener(this)

        val playButton = view.findViewById<Button>(R.id.button_play)
        playButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.logout_button  -> signOut()
            R.id.button_play    -> openSetupGame()
        }
    }

    private fun openSetupGame() {
        findNavController().navigate(R.id.action_menuFragment_to_setupGameFragment)
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity()) {
                Log.i(Config.LOGINTAG, "Logged out")
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
    }


}