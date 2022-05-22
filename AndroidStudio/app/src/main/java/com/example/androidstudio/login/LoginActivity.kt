package com.example.androidstudio.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstudio.home.MenuActivity
import com.example.androidstudio.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity(), View.OnClickListener{

    private var clientId = "8984037607-diqinm17j00uucdgkt14jb71seu6qlm1.apps.googleusercontent.com"
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)

        signInButton.setOnClickListener(this);


    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1){
//            val credential = Identity.getSignInClient(this).getSignInCredentialFromIntent(data)
//            val name = credential.displayName
//            val familyName = credential.familyName
//            val token = credential.googleIdToken
//            val id = credential.id
//            Log.i("Login", "OnActivityResult: $name\n$familyName\n$token\n$id")
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 0) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (idToken != null) {

                // Got an ID token from Google. Use it to authenticate
                // with Firebase.
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("Login", "signInWithCredential:success")
                            val user = auth.currentUser
                            signIn()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("Login", "signInWithCredential:failure", task.exception)
                        }
                    }
            } else {
                // Shouldn't happen.
                Log.i("Login", "No ID token!")
            }
            Log.i("Login", "Success Login, Logged in")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.i("Login", "signInResult:failed code=" + e.statusCode)
        }
    }

    override fun onBackPressed() {

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> {
                val signInIntent: Intent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, 0)
            }
        }
    }

    private fun signIn() {

        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0);
        finish()
    }

    override fun onStart() {
        super.onStart()
        //val account = GoogleSignIn.getLastSignedInAccount(this)
        val currentUser = auth.currentUser
        Log.i("Login", "" + currentUser?.displayName.toString())
        if (currentUser != null){
            Log.i("Login", "Existing login, Logged in")
            signIn()
        }
        else {
            Log.i("Login", "Not existing login")
        }
    }
}