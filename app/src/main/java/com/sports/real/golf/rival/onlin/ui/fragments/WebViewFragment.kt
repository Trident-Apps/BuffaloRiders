package com.sports.real.golf.rival.onlin.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.onlin.golf.rival.onlin.R
import com.onlin.golf.rival.onlin.databinding.WebViewFragmentBinding
import com.sports.real.golf.rival.onlin.ui.activites.dataStore

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WebViewFragment : Fragment() {
    private var _binding: WebViewFragmentBinding? = null
    private val binding get() = _binding!!
    lateinit var webView: WebView
    private var messageAb: ValueCallback<Array<Uri?>>? = null


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
        webView.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 7.0; SM-G930V Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36"
        webView.settings.javaScriptEnabled = true
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = false
        webView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri?>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                messageAb = filePathCallback
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "image/*"
                startActivityForResult(
                    Intent.createChooser(intent, "Image Chooser"), 1
                )

                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        isEnabled = false
                    }
                }
            })
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
            url?.let {
                if (it == "https://buffalorides.online/") {
                    findNavController().navigate(R.id.gameFragment)
                } else {
                    lifecycleScope.launch {
                        if (checkDataStore("sharedPref").isNullOrEmpty()) {
                            val dataStoreKey = stringPreferencesKey("sharedPref")
                            context?.dataStore?.edit { sharedPref ->
                                sharedPref[dataStoreKey] = "final"
                            }
                            saveUrl(url)
                        }
                    }
                }
            }
        }
    }

    private suspend fun checkDataStore(key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = context!!.dataStore.data.first()
        return preferences[dataStoreKey]
    }

    private suspend fun saveUrl(url: String) {
        val dataStoreKey = stringPreferencesKey("finalUrl")
        context!!.dataStore.edit { sharedPref ->
            sharedPref[dataStoreKey] = url
        }
    }

}
