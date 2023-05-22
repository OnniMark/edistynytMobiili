package com.example.edistynytandroidkurssi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.edistynytandroidkurssi.databinding.FragmentCityWeatherBinding
import com.example.edistynytandroidkurssi.databinding.FragmentDetailBinding
import com.example.edistynytandroidkurssi.datatypes.cityweather.CityWeather
import com.google.gson.GsonBuilder


class CityWeatherFragment : Fragment() {
    private var _binding: FragmentCityWeatherBinding? = null


    // get fragment parameters from previous fragment
    val args: CityWeatherFragmentArgs by navArgs()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCityWeatherBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Log.d("TESTI", "CityWeatherFragment START")
        Log.d("TESTI", args.lat.toString())
        Log.d("TESTI", args.lon.toString())

        // kutsuu apufunkion
        getWeatherdata()



        return root
    }

    // Luodaan apufunktio, johon voidaan eriyttää säädatan hakeminen ja käsittely
    fun getWeatherdata(){

        // Haetaan local.proterties kansiosta OpenWeather API KEY
        val API_KEY =  BuildConfig.OPENWEATHER_API_KEY

        // Eli rakennetaan Kotlinissa osoite lat/lon ja OPENWEATHER_API_KEY:n pohjalta:
        val JSON_URL = "https://api.openweathermap.org/data/2.5/weather?lat=${args.lat}&lon=${args.lon}&units=metric&appid=${API_KEY}"

        val gson = GsonBuilder().setPrettyPrinting().create()

        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, JSON_URL,
            Response.Listener { response ->

                // print the response as a whole
                // we can use GSON to modify this response into something more usable
                Log.d("TESTI", response)

                // datassa tulee vain yhden kaupungin JSON, muutetaan se GSON:illa käyttökelpoiseen muotoon
                // tämä vaatii CityWeather-luokan, joka tehdään esimerkkidatan pohjalta
                //json2kt.com -palvelussa.
                var item : CityWeather = gson.fromJson(response, CityWeather::class.java)


                // Tässä haetaan testimielessä pari muuttujaa
                // main? tarkoittaa, että jos main-olio on arvoltaan null
                // koodia ei lähdetä ajamaa väkisin läpi, koska se tilttaisi.
                // tämä liittyy null safety ominaisuuteen ohjelmointikielessä
                val temperature = item.main?.temp
                val humidity = item.main?.humidity
                val windSpeed = item.wind?.speed

                // viedään data textvieweihin, jotta saadaan ne näkyville fragment_city_weatheriin
                binding.textViewCityHumidity.text = "${humidity}%"
                binding.textViewCityTemperature.text = "${temperature}C"
                binding.textViewCityWindspeed.text = "${windSpeed}KM/H"


                // kokeillaan lokittaa tarkemmat tiedot
                Log.d("TESTI", "lämpötila: ${temperature}C, Kosteusprosentti: ${humidity}%, Tuulen nopeus: ${windSpeed}km/h")

            },
            Response.ErrorListener {
                // typically this is a connection error
                Log.d("TESTI", it.toString())
            })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                // basic headers for the data
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }

        // Add the request to the RequestQueue. This has to be done in both getting and sending new data.
        // if using this in an activity, use "this" instead of "context"
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}