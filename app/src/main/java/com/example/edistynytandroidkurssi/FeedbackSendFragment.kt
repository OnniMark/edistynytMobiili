package com.example.edistynytandroidkurssi

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.edistynytandroidkurssi.databinding.FragmentFeedbackReadBinding
import com.example.edistynytandroidkurssi.databinding.FragmentFeedbackSendBinding
import com.example.edistynytandroidkurssi.datatypes.Feedback.Feedback
import com.google.gson.GsonBuilder
import java.io.UnsupportedEncodingException


class FeedbackSendFragment : Fragment() {
    private var _binding: FragmentFeedbackSendBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedbackSendBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // kun nappia painetaan, haetaan jokainen EditTextin sisältö talteen muuttujiin.
        binding.buttonSendFeedback.setOnClickListener {
            val name = binding.editTextFeedbackName.text.toString()
            val location = binding.editTextFeedbackLocation.text.toString()
            val value = binding.editTextFeedbackValue.text.toString()

            Log.d("TESTI", "${name} - ${location} - ${value}")

            sendFeedback(name, location, value)
        }


        return root
    }

    // tehdään apufunktio, jonka avulla voidaan lähettää uusi feedback directusiin.
    fun sendFeedback(name: String, location: String, value: String) {

        //Sama osoite, kuin ReadFragmentissa
        val JSON_URL = "http://10.0.2.2:8055/items/feedback?access_token=_T2oo8RVj1dPyUXuaw75UWZWiiuY5pfO"

        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, JSON_URL,
            Response.Listener { response ->

                Log.d("TESTI", "Uusi feedback tallennettu!")

                // tyhjennetään EditTextit, jotta käyttäjä huomaa, että jotain on tapahtunut
                binding.editTextFeedbackLocation.setText("")
                binding.editTextFeedbackName.setText("")
                binding.editTextFeedbackValue.setText("")



            },
            Response.ErrorListener {
                // typically this is a connection error
                Log.d("TESTI", it.toString())
            })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }

            // let's build the new data here
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                // this function is only needed when sending data
                var body = ByteArray(0)
                try {

                    // Rakennetaan feedback-olio lennosta, ja muutetaan se GSONin avulla tekstimuotoon
                    var f : Feedback = Feedback();
                    f.location = location
                    f.name = name
                    f.value = value

                    // muutetaan Feedback-olio -> JSONiksi
                    var gson = GsonBuilder().create();
                    var newData = gson.toJson(f);


                    body = newData.toByteArray(Charsets.UTF_8)
                } catch (e: UnsupportedEncodingException) {
                    // problems with converting our data into UTF-8 bytes
                }
                return body
            }
        }

        // Add the request to the RequestQueue. This has to be done in both getting and sending new data.
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}