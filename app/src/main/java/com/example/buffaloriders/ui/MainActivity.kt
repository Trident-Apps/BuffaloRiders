package com.example.buffaloriders.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import com.example.buffaloriders.R
import com.example.buffaloriders.util.Consts
import com.onesignal.OneSignal

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sharedPref")

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        OneSignal.initWithContext(this)
        OneSignal.setAppId(Consts.ONE_SIGNAL_ID)

    }
}