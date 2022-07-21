package com.example.buffaloriders.ui.activites

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        with(Intent(this@SplashActivity, MainActivity::class.java)) {
            startActivity(this)
            this@SplashActivity.finish()
        }
    }
}