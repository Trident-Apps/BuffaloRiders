package com.sports.real.golf.rival.onlin.ui.activites


import android.app.Activity
import android.bluetooth.le.AdvertisingSet
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import com.onlin.golf.rival.onlin.R
import com.sports.real.golf.rival.onlin.util.Consts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TAG = "BuffaloViewModel"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sharedPref")

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Start Activity")

        lifecycleScope.async {
            val gadId =
                AdvertisingIdClient.getAdvertisingIdInfo(applicationContext).id.toString()
            OneSignal.initWithContext(applicationContext)
            OneSignal.setAppId(Consts.ONE_SIGNAL_ID)
            OneSignal.setExternalUserId(gadId)
        }
    }
}