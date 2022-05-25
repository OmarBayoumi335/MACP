package com.example.androidstudio.Login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstudio.R
import com.example.androidstudio.classi.Config
import com.example.androidstudio.classi.ServerHandler
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
import org.json.JSONObject


class LoginActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var serverHandler: ServerHandler

    private var clientId = Config.CLIENT_ID
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var usernameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        serverHandler = ServerHandler(applicationContext)

        // Setting the name if you have already logged in in the past
        sharedPreferences = getSharedPreferences("lastGoogleId", MODE_PRIVATE)
        val lastUid : String = sharedPreferences.getString("UID", "").toString()
        usernameEditText = findViewById<EditText>(R.id.login_username_edittext)
        serverHandler.getUserInformation(lastUid, object : ServerHandler.VolleyCallBack {
            override fun onSuccess(reply: JSONObject?) {
                usernameEditText.setText(reply?.get("username").toString())
            }
        })

        // Firebase authentication
        auth = Firebase.auth

        // Google authentication
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Google button
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
                            Log.i(Config.LOGINTAG, "signInWithCredential: success")
                            val user = auth.currentUser
                            sharedPreferences.edit().putString("UID", auth.uid).apply()
                            signIn(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(Config.LOGINTAG, "signInWithCredential: failure ", task.exception)
                        }
                    }
            } else {
                // Shouldn't happen.
                Log.e(Config.LOGINTAG, "No ID token!")
            }
            Log.i(Config.LOGINTAG, "Success Login")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(Config.LOGINTAG, "signInResult:failed code = " + e.statusCode)
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

    private fun signIn(currentUser: FirebaseUser?, fromOnStart: Boolean = false) {
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
                    Log.i(Config.LOGINTAG, "New user created")
                    val username = findViewById<EditText>(R.id.login_username_edittext).text.toString()
                    val id = createId(lastUserId)
                    val user = User(username, id)
                    database.child("Users").child(currentUser.uid).setValue(user)
                    database.child("UserLastId").setValue(id)
                } else if (!fromOnStart) {
                    serverHandler.postChangeName(auth.uid.toString(), usernameEditText.text.toString())
                }
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0);
                finish()
            }.addOnFailureListener {
                Log.e(Config.LOGINTAG, "Error getting data Firebase", it)
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
            Log.i(Config.LOGINTAG, "OnStart logged in")
            val currentUser = auth.currentUser
            signIn(currentUser)
        }
        else {
            Log.w(Config.LOGINTAG, "OnStart account not found")
        }
    }
}