package com.sports.real.golf.rival.onlin.ui.fragments

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.onlin.golf.rival.onlin.R
import com.onlin.golf.rival.onlin.databinding.LoadingFragmentBinding
import com.sports.real.golf.rival.onlin.ui.activites.dataStore
import com.sports.real.golf.rival.onlin.ui.viewmodel.BuffaloViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class LoadingFragment : Fragment() {
    private val TAG = "BuffaloViewModel"

    private var _binding: LoadingFragmentBinding? = null
    private val binding get() = _binding
    private val viewModel: BuffaloViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                TODO("Not yet implemented")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoadingFragmentBinding.inflate(inflater, container, false)
        val view = binding?.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "OnViewCreated")

        if (isSecured()){
            Log.d(TAG, "Secure Checked")

            lifecycleScope.launch(Dispatchers.IO) {
                val dataStoreValue = checkDataStoreValue("finalUrl")
                Log.d(TAG, "After Datastore")

                if (dataStoreValue == null) {
                    Log.d(TAG, "Datastore null")


                    viewModel.getDeepLink(this@LoadingFragment.requireActivity())
                    Log.d(TAG, "Init after")


                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.urlLiveData.observe(viewLifecycleOwner) {
                            Log.d(TAG, "Appsflyer init url start111231241")

                            startWebView(it)
                        }
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        startWebView(dataStoreValue.toString())
                    }
                }
            }
        } else {
            startGame()
        }
    }

    private fun isSecured(): Boolean {
        return checkRoots() && checkAdb() == "1"
    }

    private fun checkRoots(): Boolean {
        val dirs = arrayOf(
            "/sbin/",
            "/system/bin/",
            "/system/xbin/",
            "/data/local/xbin/",
            "/data/local/bin/",
            "/system/sd/xbin/",
            "/system/bin/failsafe/",
            "/data/local/"
        )
        try {
            for (dir in dirs) {
                if (File(dir + "su").exists()) return true
            }
        } catch (t: Throwable) {
        }
        return false
    }

    private fun checkAdb(): String {
        return Settings.Global.getString(activity?.contentResolver, Settings.Global.ADB_ENABLED)
            ?: "null"
    }

    private suspend fun checkDataStoreValue(key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = context!!.dataStore.data.first()
        return preferences[dataStoreKey]
    }

    private fun startGame() {
        findNavController().navigate(R.id.gameFragment)
    }

    private fun startWebView(url: String) {
        val bundle = bundleOf("fullUrl" to url)
        findNavController().navigate(R.id.webViewFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}