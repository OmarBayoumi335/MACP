package com.example.androidstudio.login

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.androidstudio.Login.LoginActivity
import com.example.androidstudio.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var splashScreenName: TextView
    private lateinit var splashScreenLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splashScreenName = findViewById(R.id.splash_screen_name)
        splashScreenLayout = findViewById(R.id.background_layout_splashscreen)

        val intent = Intent(this, LoginActivity::class.java)

        GlobalScope.launch(Dispatchers.Main) {
            fadeIn()
            wait(2700L)
            fadeOut()
            wait(1000L)
            startActivity(intent)
            overridePendingTransition(0, 0);
            finish()
        }
    }

    private suspend fun wait(time: Long) {
        delay(time)
    }
    private fun fadeIn() {
        val fadeIn = AlphaAnimation(0.0f, 1.0f)
        fadeIn.duration = 2000
        fadeIn.fillAfter = true
        splashScreenName.startAnimation(fadeIn)
        splashScreenLayout.startAnimation(fadeIn)
    }
    private fun fadeOut() {
        val fadeOut = AlphaAnimation(1.0f, 0.0f)
        fadeOut.duration = 1000
        fadeOut.fillAfter = true
        splashScreenName.startAnimation(fadeOut)
    }
}