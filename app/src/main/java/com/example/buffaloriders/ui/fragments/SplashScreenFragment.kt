package com.example.buffaloriders.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.buffaloriders.R
import com.example.buffaloriders.databinding.SplashFragmentBinding

class SplashScreenFragment : Fragment() {

    private var _binding: SplashFragmentBinding? = null
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = SplashFragmentBinding.inflate(inflater, container, false)
        val view = binding?.root


        return view
    }

    private fun splashScreenDelay() {
        Handler(Looper.myLooper()!!).postDelayed({
            findNavController().navigate(R.id.loadingFragment)
        }, 2000)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        splashScreenDelay()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}