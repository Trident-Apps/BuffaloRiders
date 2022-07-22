package com.sports.real.golf.rival.onlin.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import com.sports.real.golf.rival.onlin.util.Consts
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
                    sendOneSignalTag("null", data)
                    urlLiveData.postValue(createUrl("null", data, activity))

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
        } else if (deepLink != "null") {
            OneSignal.sendTag("key2,", deepLink.replace("myapp://", "").substringBefore("/"))
        } else if (campaign != "null") {
            OneSignal.sendTag("key2", campaign.substringBefore("_"))
        }
    }

    private fun createUrl(
        deepLink: String,
        data: MutableMap<String, Any>?,
        activity: Context
    ): String {
        val gadId =
            AdvertisingIdClient.getAdvertisingIdInfo(activity.applicationContext).id.toString()
        OneSignal.setExternalUserId(gadId)
        val baseUrl = Consts.BASE_URL
        val url = baseUrl.toUri().buildUpon().apply {
            appendQueryParameter(
                Consts.SECURE_GET_PARAMETR,
                Consts.SECURE_KEY
            )
            appendQueryParameter(
                Consts.DEV_TMZ_KEY,
                TimeZone.getDefault().id
            )
            appendQueryParameter(Consts.GADID_KEY, gadId)
            appendQueryParameter(Consts.DEEPLINK_KEY, deepLink)
            appendQueryParameter(
                Consts.SOURCE_KEY,
                data?.get("media_source").toString()
            )
            appendQueryParameter(
                Consts.AF_ID_KEY,
                AppsFlyerLib.getInstance().getAppsFlyerUID(activity.applicationContext)
            )
            appendQueryParameter(
                Consts.ADSRT_KEY,
                data?.get("adset_id").toString()
            )
            appendQueryParameter(
                Consts.CAMPAIGN_ID_KEY,
                data?.get("campaign_id").toString()
            )
            appendQueryParameter(
                Consts.APP_CAMPAIGN_KEY,
                data?.get("campaign").toString()
            )
            appendQueryParameter(
                Consts.ADSRT_KEY,
                data?.get("adset").toString()
            )
            appendQueryParameter(
                Consts.ADGROUP_KEY,
                data?.get("adgroup").toString()
            )
            appendQueryParameter(
                Consts.ORIG_CONST_KEY,
                data?.get("orig_cost").toString()
            )
            appendQueryParameter(
                Consts.AF_SITE_ID_KEY,
                data?.get("af_siteid").toString()
            )
        }.toString()
        return url
    }
}
