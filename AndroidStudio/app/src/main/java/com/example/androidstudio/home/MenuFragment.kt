package com.example.androidstudio.Home

import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidstudio.Login.LoginActivity
import com.example.androidstudio.R
import com.example.androidstudio.home.profile.ProfileFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class MenuFragment : Fragment(), View.OnClickListener {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var clientId = "8984037607-diqinm17j00uucdgkt14jb71seu6qlm1.apps.googleusercontent.com"

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

        val profileButton = view.findViewById<ImageButton>(R.id.button_profile)
        profileButton.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.logout_button  -> signOut()
            R.id.button_play    -> openSetupGame()
            R.id.button_profile -> openProfile()
        }
    }

    private fun openProfile() {
        ProfileFragment().show(requireActivity().supportFragmentManager, "Menu->Profile")
    }

    private fun openSetupGame() {
        findNavController().navigate(R.id.action_menuFragment_to_setupGameFragment)
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity()) {
                Log.i("Login", "Logged out")
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
    }


}