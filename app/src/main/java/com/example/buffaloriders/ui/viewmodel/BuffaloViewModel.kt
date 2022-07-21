package com.example.buffaloriders.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.example.buffaloriders.R
import com.example.buffaloriders.util.Consts
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


private const val TAG = "BuffaloViewModel"

class BuffaloViewModel(app: Application) : AndroidViewModel(app) {
    var urlLiveData: MutableLiveData<String> = MutableLiveData()

    init {
        initUrl()
    }

    private fun initUrl() {
        viewModelScope.launch {
            val appsFlyerDeferred: Deferred<MutableMap<String, Any>?> = async { getAppsFlyer() }
            Log.d(TAG, "AppsFlyer deferred -$appsFlyerDeferred")

            val deepLinkDeferred: Deferred<String> = async { getDeepLink() }
            Log.d(TAG, "Deeplink deferred -$deepLinkDeferred")

            sendOneSignalTag(deepLinkDeferred.await(), appsFlyerDeferred.await())
            val logOnesignal =
                sendOneSignalTag(deepLinkDeferred.await(), appsFlyerDeferred.await()).toString()
            Log.d(TAG, " OneSignal tag $logOnesignal")

            withContext(Dispatchers.Default) {
                val url = createUrl(deepLinkDeferred.await(), appsFlyerDeferred.await())

                Log.d(TAG, "Created url $url")
                urlLiveData.postValue(url)
            }
        }
    }

    private suspend fun getDeepLink(): String {
        return suspendCoroutine { continuation ->
            AppLinkData.fetchDeferredAppLinkData(getApplication()) {
                continuation.resume(it?.targetUri.toString())
            }
        }
    }

    private suspend fun getAppsFlyer(): MutableMap<String, Any>? {
        return suspendCoroutine { continuation ->
            AppsFlyerLib.getInstance().init(
                (Consts.AF_ID_KEY),
                object : AppsFlyerConversionListener {
                    override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                        continuation.resume(p0)
                        Log.d(TAG, "Appsflyer success")
                    }

                    override fun onConversionDataFail(p0: String?) {
                        Log.d(TAG, "Appsflyer error")

                    }

                    override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                        TODO("Not yet implemented")
                    }

                    override fun onAttributionFailure(p0: String?) {
                        TODO("Not yet implemented")
                    }

                },
                getApplication()
            )
            AppsFlyerLib.getInstance().start(getApplication())
        }
    }

    private fun sendOneSignalTag(deepLink: String, data: MutableMap<String, Any>?) {
        val campaign = data?.get("campaign").toString()

        if (campaign == "null" && deepLink == "null") {
            OneSignal.sendTag("key2", "organic")
        } else if (deepLink !== " null") {
            OneSignal.sendTag("Key2,", deepLink.replace("myapp://", "").substringBefore("/"))
        } else if (campaign !== "null") {
            OneSignal.sendTag("key2", campaign.substringBefore("_"))
        }
    }

    private fun createUrl(deepLink: String, data: MutableMap<String, Any>?): String {
        val app = getApplication<Application>().applicationContext
        val gadId = AdvertisingIdClient.getAdvertisingIdInfo(getApplication()).id.toString()
        val baseUrl = R.string.base_url.toString()
        val url = baseUrl.toUri().buildUpon().apply {
            appendQueryParameter(
                app.resources.getString(R.string.secure_get_parametr),
                app.resources.getString(R.string.secure_key)
            )
            appendQueryParameter(
                app.resources.getString(R.string.dev_tmz_key),
                TimeZone.getDefault().id
            )
            appendQueryParameter(app.resources.getString(R.string.gadid_key), gadId)
            appendQueryParameter(app.resources.getString(R.string.deeplink_key), deepLink)
            appendQueryParameter(
                app.resources.getString(R.string.source_key),
                data?.get("media_source").toString()
            )
            appendQueryParameter(
                app.resources.getString(R.string.af_id_key),
                AppsFlyerLib.getInstance().getAppsFlyerUID(getApplication())
            )
            appendQueryParameter(
                app.resources.getString(R.string.adset_id_key),
                data?.get("adset_id").toString()
            )
            appendQueryParameter(
                app.resources.getString(R.string.campaign_id_key),
                data?.get("campaign_id").toString()
            )
            appendQueryParameter(
                app.resources.getString(R.string.app_campaign_key),
                data?.get("campaign").toString()
            )
            appendQueryParameter(
                app.resources.getString(R.string.adset_key),
                data?.get("adset").toString()
            )
            appendQueryParameter(
                app.resources.getString(R.string.adgroup_key),
                data?.get("adgroup").toString()
            )
            appendQueryParameter(
                app.resources.getString(R.string.orig_cost_key),
                data?.get("orig_cost").toString()
            )
            appendQueryParameter(
                app.resources.getString(R.string.af_siteid_key),
                data?.get("af_siteid").toString()
            )
        }.toString()
        return url
    }
}