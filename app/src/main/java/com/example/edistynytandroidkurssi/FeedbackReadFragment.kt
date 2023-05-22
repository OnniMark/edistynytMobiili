package com.example.edistynytandroidkurssi

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.edistynytandroidkurssi.databinding.FragmentDataBinding
import com.example.edistynytandroidkurssi.databinding.FragmentFeedbackReadBinding
import com.example.edistynytandroidkurssi.datatypes.Feedback.Feedback
import com.google.gson.GsonBuilder
import org.json.JSONObject


class FeedbackReadFragment : Fragment() {

    private var _binding: FragmentFeedbackReadBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedbackReadBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // haetaan feedback data Directusista heti, kun ladataan feedback
        getFeedbacks()

        return root
        }

        fun getFeedbacks(){

            // Alustava data feedbäkille + GSON:in alustus
            var feedbacks : List<Feedback> = emptyList();
            val gson = GsonBuilder().setPrettyPrinting().create()

            //Laitetaan local propertiesiin
            //DIRECTUS_ADDRESS
            //Haetaan koodissa BuildConfig.DIRECTUS_ADDRESS
            // Lopuksi käunnistä sovellus kerran uudelleen, jotta BuildConfig päivittyy oikein.
            val JSON_URL = "http://10.0.2.2:8055/items/feedback?access_token=_T2oo8RVj1dPyUXuaw75UWZWiiuY5pfO"

            // Request a string response from the provided URL.
            val stringRequest: StringRequest = object : StringRequest(
                Request.Method.GET, JSON_URL,
                Response.Listener { response ->

                    // Näytetään raakadata LogCatissa
                    Log.d("TESTI", response)

                    // koska Directus tallentaa varsinaisen datan kenttään "data", pitää meidän
                    // suodattaa alkuperäistä JSONia hieman
                    val jObject = JSONObject(response)
                    val jArray = jObject.getJSONArray("data")

                    // muutetaan lista Feedback dataa kotlin-dataksi
                    feedbacks = gson.fromJson(jArray.toString() , Array<Feedback>::class.java).toList()

                    for(item : Feedback in feedbacks) {
                        Log.d("TESTI", item.location.toString())
                    }

                    // Luodaan adapteri ListViewille
                    val adapter = ArrayAdapter(activity as Context, android.R.layout.simple_list_item_1, feedbacks)

                    // Muista myös lisätä listview fragmentin ulkoasuun (xml)
                    binding.listViewFeedbacks.adapter = adapter

                },
                Response.ErrorListener {
                    // typically this is a connection error
                    Log.d("ADVTECH", it.toString())
                })
            {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    // we have to specify a proper header, otherwise Apigility will block our queries!
                    // define we are after JSON data!
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    return headers
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
