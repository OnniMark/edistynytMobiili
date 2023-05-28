package com.example.edistynytandroidkurssi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.edistynytandroidkurssi.databinding.FragmentDataBinding
import com.example.edistynytandroidkurssi.databinding.FragmentWeatherStationBinding
import com.example.edistynytandroidkurssi.datatypes.weatherstation.WeatherStation
import com.google.gson.GsonBuilder
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import java.text.SimpleDateFormat
import java.util.*


class WeatherStationFragment : Fragment() {
    private var _binding: FragmentWeatherStationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.


    private lateinit var client: Mqtt3AsyncClient

    // apufunktio/ metodi jolla yhdistetään sääaseman topicciin
    // jos yhteys onnistui aiemmin
    fun subscribeToTopic()
    {
        // Alustetaan GSON
        val gson = GsonBuilder().setPrettyPrinting().create()

        client.subscribeWith()
            .topicFilter(BuildConfig.MQTT_TOPIC)
            .callback { publish ->

                // Muutetaan raakadata tekstiksi eli tässä JSONIIN
                var result = String(publish.getPayloadAsBytes())

                // TRY/CATCH => koodi joka saattaa tiltata laitetaan Try koodin sisälle
                // Catch hoitaa virhetilanteet
                //  nyt MQTT:stä tulee välillä diagnostiikkadataa, mikä rikkoo koodin
                // try/catch estää ohjelman tilttaamisen
                try {
                // muutetaan vastaanotettu data JSONista -> WeatherStation  luokan olioksi
                var item : WeatherStation = gson.fromJson(result, WeatherStation::class.java)
                Log.d ("ADVTECH", item.d.get1().v.toString() + "C")

                    // asetetaan tekstimuuttuja käyttöliittymään
                    val temperature = item.d.get1().v
                    var humidity = item.d.get3().v
                    var text = "Temperature: ${temperature}℃"

                    text += "\n"
                    text += "Humidity: ${humidity}%"


                    val sdf = SimpleDateFormat(" HH:mm:ss")
                    val currentDate = sdf.format(Date())

                    var dataText : String = "${currentDate} - Temperature: ${temperature}℃ - Humidity: ${humidity}%"

                    // koska MQTT plugin ajaa koodia ja käsittelee dataa
                    // tausta-ajalla omassa säikeessään  eli threadissa
                    // joudumme laittamaan ulkoasuun liittyvän koodin runOnUiThread-blokin
                    // sisälle. Muutoin tulee virhe, että koodit toimivat eri säikeissä
                    activity?.runOnUiThread {
                        binding.textViewWeatherText.text = text
                        // liitetään luotu mittari fragmenttiin
                        binding.speedViewTemperature.speedTo(temperature.toFloat())
                        binding.customtemperatureviewTemperature.changeTemperature(temperature.toInt())

                        binding.LatestDataViewTest.addData(dataText)
                    }


                }
                catch (e: Exception ) {
                    Log.d("TESTI", e.message.toString())
                    Log.d("Testi", "Saattaa olla myös diagnostiikkadataa")
                }

            }
            .send()
            .whenComplete { subAck, throwable ->
                if (throwable != null) {
                    // Handle failure to subscribe
                    Log.d("TESTI", "Subscribe failed.")
                } else {
                    // Handle successful subscription, e.g. logging or incrementing a metric
                    Log.d("TESTI", "Subscribed!")
                }
            }
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherStationBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //laitetaan local protertiesiin ja satunnainen tekstihäntä liitetään
        // perään UUID kirjaston avulla
        client = MqttClient.builder()
            .useMqttVersion3()
            .sslWithDefaultConfig()
            .identifier(BuildConfig.MQTT_CLIENT_ID + UUID.randomUUID().toString())
            .serverHost(BuildConfig.MQTT_BROKER)
            .serverPort(8883)
            .buildAsync()


        // yhdistetääm käyttäjätiedoilla
        client.connectWith()
            .simpleAuth()
            .username(BuildConfig.MQTT_USERNAME)
            .password(BuildConfig.MQTT_PASSWORD.toByteArray())
            .applySimpleAuth()
            .send()
            .whenComplete { connAck: Mqtt3ConnAck?, throwable: Throwable? ->
                if (throwable != null) {
                    Log.d("ADVTECH", "Connection failure.")
                } else {
                    // Setup subscribes or start publishing
                    subscribeToTopic()
                }
            }


        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // tällä suljetaan MQTT yhteys mikäli fragmen suljetaan.
        client.disconnect()
    }
}