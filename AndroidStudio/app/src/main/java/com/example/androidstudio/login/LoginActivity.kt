package com.example.androidstudio.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstudio.R
import com.example.androidstudio.classi.User
import com.example.androidstudio.home.MenuActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity(), View.OnClickListener{

    private var clientId = "8984037607-diqinm17j00uucdgkt14jb71seu6qlm1.apps.googleusercontent.com"
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        val signInButton = findViewById<SignInButton>(R.id.login_sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)

        signInButton.setOnClickListener(this);

        for (i in 0 until signInButton.getChildCount()) {
            val v: View = signInButton.getChildAt(i)
            if (v is TextView) {
                val tv = v
                tv.setText(R.string.google_button)
            }
        }
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
                            signIn(user)
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
            R.id.login_sign_in_button -> {
                val signInIntent: Intent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, 0)
            }
        }
    }

    private fun signIn(currentUser: FirebaseUser?) {
        database = Firebase.database.reference
        if (currentUser != null) {
            database.get().addOnSuccessListener {
                var exist = false
                var lastUserId = ""
                for (entry in it.children){
                    if(entry.key == "UserLastId"){
                        lastUserId = entry.value as String
                    } else if(entry.key == "Users"){
                        for (single_user in entry.children) {
                            if(single_user.key == currentUser.uid){
                                exist = true
                                break
                            }
                        }
                    } else {
                        continue
                    }
                }
                if(!exist){
                    Log.i("Login", "User created")
                    val username = findViewById<EditText>(R.id.login_username_edittext).text.toString()
                    val id = createId(lastUserId)
                    val user = User(username, id)
//                    user.addFriend(User("sadfa1", "#gasdgasg1"))
//                    user.addFriend(User("sadfa2", "#gasdgasg2"))
//                    user.addFriend(User("sadfa3", "#gasdgasg3"))
//                    user.addFriend(User("sadfa4", "#gasdgasg4"))
//                    user.addPendingFriendRequest(User("adfa1", "#asddasf1"))
//                    user.addPendingFriendRequest(User("adfa2", "#asddasf2"))
//                    user.addPendingFriendRequest(User("adfa3", "#asddasf3"))
//                    user.addPendingFriendRequest(User("adfa4", "#asddasf4"))
//                    database.child("Users").child("utente2").setValue(user)
                    database.child("Users").child(currentUser.uid).setValue(user)
                    database.child("UserLastId").setValue(id)
                }
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0);
                finish()
            }.addOnFailureListener {
                Log.e("Login", "Error getting data", it)
            }
        }
    }

    private fun createId(id: String?): String {
        var output = ""
        var carryover = true
        if (id != null) {
            for(i in id.length-1 downTo 1 step 1){
                var c = id[i].toString()
                if(carryover){
                    val n = decodeId(c[0]) + 1
                    c = "0"
                    if(n < 62){
                        c = encodeId(n)
                        carryover = false
                    }
                }
                output = output.plus(c)
            }
        }
        output = output.plus("#")
        output = output.reversed()
        return output
    }

    private fun decodeId(c: Char): Int{
        val hashcode = c.hashCode()
        var n = 0
        if(hashcode in 48..57) { // numbers
            n = hashcode - 48
        }

        if(hashcode in 97..122) { // lowercase
            n = hashcode - 97 + 10
        }

        if(hashcode in 65..90) { // uppercase
            n = hashcode - 65 + 10 + 26
        }
        return n
    }

    private fun encodeId(n: Int): String{
        var index = 0
        var x = ""
        if(n < 10) { // numbers
            index = n + 48
            x = index.toChar().toString()
        }

        if(n in 10..10+25) { // lowercase
            index = n - 10 + 97
            x = index.toChar().toString()
        }

        if(n >= 10 +26) { // uppercase
            index = n - (10 + 26) + 65
            x = index.toChar().toString()
        }
        return x
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null){
            Log.i("Login", "Existing login, Logged in")
            val currentUser = auth.currentUser
            signIn(currentUser)
        }
        else {
            Log.i("Login", "Not existing login")
        }
    }
}