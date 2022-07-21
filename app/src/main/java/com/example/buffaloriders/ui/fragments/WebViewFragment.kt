package com.example.buffaloriders.ui.fragments

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.buffaloriders.databinding.WebViewFragmentBinding
import com.example.buffaloriders.ui.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WebViewFragment : Fragment() {
    private var _binding: WebViewFragmentBinding? = null
    private val binding get() = _binding!!
    lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WebViewFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        webView = binding.webView
        arguments?.getString("fullUrl")?.let { webView.loadUrl(it) }
        webView.webViewClient = LocalClient()
        webView.settings.javaScriptEnabled = true
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = false
        webView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {

                val newWebView = WebView(context!!.applicationContext)
                newWebView.settings.javaScriptEnabled = true
                newWebView.webChromeClient = this
                newWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                newWebView.settings.domStorageEnabled = true
                newWebView.settings.setSupportMultipleWindows(true)
                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                return true
            }

        }
        return view
    }


    private inner class LocalClient : WebViewClient() {
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)

        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            lifecycleScope.launch {
                if (checkDataStore("sharedPref")) {
                    saveUrl(url!!)
                }
            }
        }
    }

    private suspend fun checkDataStore(key: String): Boolean {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = context!!.dataStore.data.first()
        return preferences[dataStoreKey] == null
    }

    private suspend fun saveUrl(url: String) {
        val dataStoreKey = stringPreferencesKey("finalUrl")
        context!!.dataStore.edit { sharedPref ->
            sharedPref[dataStoreKey] = url
        }
    }

}
