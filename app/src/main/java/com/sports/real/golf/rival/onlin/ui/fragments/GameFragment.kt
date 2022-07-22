package com.sports.real.golf.rival.onlin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.onlin.golf.rival.onlin.R
import com.onlin.golf.rival.onlin.databinding.GameFragmentBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var _binding: GameFragmentBinding? = null
    private val binding get() = _binding
    private var points = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = GameFragmentBinding.inflate(inflater, container, false)
        val view = binding?.root


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOf(
            binding!!.imageView,
            binding!!.imageView2,
            binding!!.imageView3,
            binding!!.imageView4,
            binding!!.imageView5
        ).forEach { imageView ->
            imageView.setOnClickListener { view ->
                onClick(view as ImageView)
            }
        }
        lifecycleScope.launch {
            repeat(20) {
                delay(1000)
                shuffle()
            }
        }

    }

    private fun shuffle() {
        val listOfImages = listOf(
            R.drawable.icon1,
            R.drawable.icon2,
            R.drawable.icon3,
            R.drawable.icon4,
            R.drawable.icon5
        ).shuffled()
        binding!!.imageView.setImageResource(listOfImages[0])
        binding!!.imageView.tag = listOfImages[0]
        binding!!.imageView.isClickable = true
        binding!!.imageView2.setImageResource(listOfImages[1])
        binding!!.imageView2.tag = listOfImages[1]
        binding!!.imageView2.isClickable = true
        binding!!.imageView3.setImageResource(listOfImages[2])
        binding!!.imageView3.tag = listOfImages[2]
        binding!!.imageView3.isClickable = true
        binding!!.imageView4.setImageResource(listOfImages[3])
        binding!!.imageView4.tag = listOfImages[3]
        binding!!.imageView4.isClickable = true
        binding!!.imageView5.setImageResource(listOfImages[4])
        binding!!.imageView5.tag = listOfImages[4]
        binding!!.imageView5.isClickable = true

    }

    private fun onClick(view: ImageView) {
        if (view.tag == R.drawable.icon5) {
            view.isClickable = false
            points++
            binding!!.score.text = "${points}/20"
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}