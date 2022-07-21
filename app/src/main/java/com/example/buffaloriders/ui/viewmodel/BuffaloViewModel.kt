package com.example.buffaloriders.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.example.buffaloriders.R
import com.example.buffaloriders.util.Consts
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import java.util.*


private const val TAG = "BuffaloViewModel"

class BuffaloViewModel(app: Application) : AndroidViewModel(app) {
    var urlLiveData: MutableLiveData<String> = MutableLiveData()


    fun getDeepLink(activity: Activity) {
        Log.d(TAG, " deep started")

        AppLinkData.fetchDeferredAppLinkData(activity) {
            Log.d(TAG, " deep + ${it?.targetUri.toString()}")

            when {
                it?.targetUri.toString() == "null" -> {
                    Log.d(TAG, " apps started")
                    getAppsFlyer(activity)
                }
                else -> {
                    urlLiveData.postValue(createUrl(it?.targetUri.toString(), null, activity))
                    sendOneSignalTag(it?.targetUri.toString(), null)
                }
            }

        }
    }


    private fun getAppsFlyer(activity: Activity) {
        Log.d(TAG, " apps started2")

        AppsFlyerLib.getInstance().init(
            Consts.APPS_DEV_KEY,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                    Log.d(TAG, " apps started3")
                    urlLiveData.postValue(createUrl("null", data, activity))
                    sendOneSignalTag("null", data)
                }

                override fun onConversionDataFail(message: String?) {
                    Log.d(TAG, " apps started4")

                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}
                override fun onAttributionFailure(p0: String?) {
                    Log.d(TAG, " apps started5")

                }
            },
            activity
        )
        AppsFlyerLib.getInstance().start(activity)
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

    private fun createUrl(
        deepLink: String,
        data: MutableMap<String, Any>?,
        activity: Context
    ): String {
        val app = activity.applicationContext
        val gadId =
            AdvertisingIdClient.getAdvertisingIdInfo(activity.applicationContext).id.toString()
        OneSignal.setExternalUserId(gadId)
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
                AppsFlyerLib.getInstance().getAppsFlyerUID(activity.applicationContext)
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
