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
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.home.MenuActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageView


class LoginActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var serverHandler: ServerHandler

    private var clientId = Config.CLIENT_ID
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var auth: FirebaseAuth

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var usernameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        serverHandler = ServerHandler(applicationContext)

        // Firebase authentication
        auth = Firebase.auth

        // Google authentication
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Setting the name if you have already logged in in the past
        sharedPreferences = getSharedPreferences("lastId", MODE_PRIVATE)

        val lastUsername : String = sharedPreferences.getString("ID", "").toString()
        Log.i(Config.LOGIN_TAG, lastUsername)
        usernameEditText = findViewById(R.id.login_username_edittext)
        usernameEditText.setText(lastUsername)
//        serverHandler.apiCall(
//            Config.GET,
//            Config.GET_USERNAME,
//            userId = lastId,
//            callBack = object : ServerHandler.VolleyCallBack {
//                override fun onSuccess(reply: JSONObject?) {
//                    usernameEditText.setText(reply?.get("username").toString())
//                }
//            })

        // Google button
        val signInButton = findViewById<SignInButton>(R.id.login_sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener(this);
        for (i in 0 until signInButton.childCount) {
            val v: View = signInButton.getChildAt(i)
            if (v is TextView) {
                v.setText(R.string.google_button)
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
                            Log.i(Config.LOGIN_TAG, "signInWithCredential: success")
                            val user = auth.currentUser
                            signIn(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(Config.LOGIN_TAG, "signInWithCredential: failure ", task.exception)
                        }
                    }
            } else {
                // Shouldn't happen.
                Log.e(Config.LOGIN_TAG, "No ID token!")
            }
            Log.i(Config.LOGIN_TAG, "Success Login")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(Config.LOGIN_TAG, "signInResult:failed code = " + e.statusCode)
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
        loading()
        if (currentUser != null) {
            serverHandler.apiCall(
                Config.GET,
                Config.GET_USER_EXIST,
                googleUserId = currentUser.uid,
                callBack = object : ServerHandler.VolleyCallBack {
                    override fun onSuccess(reply: JSONObject?) {
                        val exist: Boolean? = reply?.getBoolean("exist")
                        val userId: String = reply?.get("userId").toString()
                        if (!exist!!) {  // User not exist
                            Log.i(Config.LOGIN_TAG, "New user created")
                            val username = findViewById<EditText>(R.id.login_username_edittext).text.toString()
                            serverHandler.apiCall(
                                Config.PUT,
                                Config.PUT_NEW_USER,
                                googleUserId = currentUser.uid,
                                username = username,
                                callBack = object : ServerHandler.VolleyCallBack{
                                    override fun onSuccess(reply: JSONObject?) {
                                        val newUserId = reply?.get("userId").toString()
                                        sharedPreferences.edit().putString("ID", username).apply()
                                        launchMenu(newUserId)
                                    }
                                })
                        } else if (!fromOnStart) {  // User exist but login
                            val username = findViewById<EditText>(R.id.login_username_edittext).text.toString()
                            sharedPreferences.edit().putString("ID", username).apply()
                            serverHandler.apiCall(
                                Config.POST,
                                Config.POST_CHANGE_NAME,
                                userId = userId,
                                newName = username
                            )

                            launchMenu(userId)
                        } else {  // User exist and logged in automatically
//                            val lastId = sharedPreferences.getString("ID", "").toString()
                            launchMenu(userId)
                        }
                    }
            })
        }
    }

    private fun launchMenu(userId: String) {
        serverHandler.apiCall(
            Config.GET,
            Config.GET_USER,
            userId = userId,
            callBack = object : ServerHandler.VolleyCallBack {
                override fun onSuccess(reply: JSONObject?) {
                    val userJsonString = reply.toString()
                    val intent = Intent(applicationContext, MenuActivity::class.java)
                    intent.putExtra("user", userJsonString)
                    startActivity(intent)
                    overridePendingTransition(0, 0);
                    finish()
                }
            },
            callBackError = object : ServerHandler.VolleyCallBackError {
                override fun onError() {
                    launchMenu(userId)
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null){
            Log.i(Config.LOGIN_TAG, "OnStart logged in")
            val currentUser = auth.currentUser
            signIn(currentUser, true)
        }
        else {
            Log.w(Config.LOGIN_TAG, "OnStart account not found")
        }
    }

    private fun loading() {
        val loginTitleTextview = findViewById<TextView>(R.id.login_title_textview)
        val loginUsernameTextField = findViewById<TextInputLayout>(R.id.login_username_textfield)
        val loginHintTextview = findViewById<TextView>(R.id.login_hint_textview)
        val loginSignInButton = findViewById<SignInButton>(R.id.login_sign_in_button)
        val accessWithGoogleTextview = findViewById<TextView>(R.id.access_with_google_textview)
        val loginGif = findViewById<GifImageView>(R.id.login_gif)
        val loginLoading = findViewById<TextView>(R.id.login_loading)
        loginTitleTextview.visibility = View.GONE
        loginUsernameTextField.visibility = View.GONE
        loginHintTextview.visibility = View.GONE
        loginSignInButton.visibility = View.GONE
        accessWithGoogleTextview.visibility = View.GONE
        loginGif.visibility = View.VISIBLE
        loginLoading.visibility = View.VISIBLE
    }
}