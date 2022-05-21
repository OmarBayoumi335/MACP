package com.example.androidstudio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.SignInButton


class LoginActivity : AppCompatActivity(), View.OnClickListener{

//    private var clientId = "180970966367-9crd69030mbdbp3a0nc1bv1j978q3v1o.apps.googleusercontent.com"
    private var clientId = "180970966367-m16mbbede27778sr85ch1noa1i8ograa.apps.googleusercontent.com"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)

        signInButton.setOnClickListener(this);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1){
            val credential = Identity.getSignInClient(this).getSignInCredentialFromIntent(data)
            val name = credential.displayName
            val familyName = credential.familyName
            val token = credential.googleIdToken
            val id = credential.id
            Log.i("info", "OnActivityResult: $name\n$familyName\n$token\n$id")
        }
    }

    override fun onBackPressed() {

    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.sign_in_button -> {
                val request = GetSignInIntentRequest.builder().setServerClientId(clientId).build()

                val intent = Identity.getSignInClient(this).getSignInIntent(request)

                intent.addOnSuccessListener {
                        success -> startIntentSenderForResult(success.intentSender,
                                                            1,
                                                            null,
                                                            0,
                                                            0,
                                                            0,
                                                            null)
                    Log.i("info", "OnSuccessListener:")
                }
                intent.addOnFailureListener {
                        e -> Log.i("info", "OnFailureListener: $e")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}