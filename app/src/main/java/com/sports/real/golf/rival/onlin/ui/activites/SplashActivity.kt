package com.sports.real.golf.rival.onlin.ui.activites

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(Intent(this@SplashActivity, MainActivity::class.java)) {
            startActivity(this)
            this@SplashActivity.finish()
        }
    }
}