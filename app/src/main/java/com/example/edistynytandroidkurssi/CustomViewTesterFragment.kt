package com.example.edistynytandroidkurssi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.edistynytandroidkurssi.databinding.FragmentCustomViewTesterBinding
import io.reactivex.internal.observers.BasicIntQueueDisposable


class CustomViewTesterFragment : Fragment() {
    private var _binding: FragmentCustomViewTesterBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomViewTesterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // nostaa nopeuden 100kmh
        binding.speedView.speedTo(35f)

        binding.customtemperatureviewTest.changeTemperature(14)


        // kun nappia painetaan vaihdetaan random lämpötila
        binding.buttonSetRandomTemperature.setOnClickListener {
            val randomtemperature : Int = (-40..40).random()
            binding.customtemperatureviewTest.changeTemperature(randomtemperature)
            Log.d("ADVTECH", randomtemperature.toString())
        }

        // testibutton jolla lisätään satunnaisia viestejö
        binding.buttonAddData.setOnClickListener {
            val randomValue : Int = (1..100).random()
            binding.LatestDataViewTest.addData("Testing" + randomValue.toString())
        }

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



