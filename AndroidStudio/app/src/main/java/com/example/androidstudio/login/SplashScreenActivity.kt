package com.example.androidstudio.Login

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstudio.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val splashScreenName = findViewById<TextView>(R.id.splash_screen_name)

        val fadeIn = AlphaAnimation(0.0f, 1.0f)
        val fadeOut = AlphaAnimation(1.0f, 0.0f)
        splashScreenName.startAnimation(fadeIn)
        splashScreenName.startAnimation(fadeOut)
        fadeIn.duration = 1200
        fadeIn.fillAfter = true
        fadeOut.duration = 1200
        fadeOut.fillAfter = true
        fadeOut.startOffset = 4200 + fadeIn.startOffset

        val intent = Intent(this, LoginActivity::class.java)

        GlobalScope.launch(Dispatchers.Main) {
            wait()
            startActivity(intent)
            overridePendingTransition(0, 0);
            finish()
        }
    }

    suspend fun wait() {
//        delay(6600)
    }
}