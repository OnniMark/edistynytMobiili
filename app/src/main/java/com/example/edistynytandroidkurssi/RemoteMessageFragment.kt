package com.example.edistynytandroidkurssi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.edistynytandroidkurssi.databinding.FragmentDataBinding
import com.example.edistynytandroidkurssi.databinding.FragmentRemoteMessageBinding
import com.example.edistynytandroidkurssi.datatypes.weatherstation.WeatherStation
import com.google.gson.GsonBuilder
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import kotlin.random.Random


class RemoteMessageFragment : Fragment() {
    private var _binding: FragmentRemoteMessageBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var client: Mqtt3AsyncClient

    // apufunktio/ metodi jolla yhdistetään sääaseman topicciin
    // jos yhteys onnistui aiemmin
    fun subscribeToTopic()
    {


        client.subscribeWith()
            .topicFilter(BuildConfig.HIVEMQ_TOPIC)
            .callback { publish ->

                // Muutetaan raakadata tekstiksi eli tässä JSONIIN
                var result = String(publish.getPayloadAsBytes())


                activity?.runOnUiThread {
                    binding.textViewRemoteMessage.text = result
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



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRemoteMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // Tehdään testinappi, jolla saadaan lähetetttyä satunnainen viesti samaa topicciin
        binding.buttonSendRemoteMessage.setOnClickListener {
            var randomNumber = Random.nextInt(0, 100)
            var stringPayload = "Hello world! " + randomNumber.toString()

            client.publishWith()
                .topic(BuildConfig.HIVEMQ_TOPIC)
                .payload(stringPayload.toByteArray())
                .send()
        }

        // Käytetään aina samaa client idtä, ettei ilmaiset
        // client id:t kulu
        client = MqttClient.builder()
            .useMqttVersion3()
            .sslWithDefaultConfig()
            .identifier("android2023test123")
            .serverHost(BuildConfig.HIVEMQ_BROKER)
            .serverPort(8883)
            .buildAsync()


        // yhdistetääm käyttäjätiedoilla
        client.connectWith()
            .simpleAuth()
            .username(BuildConfig.HIVEMQ_USERNAME)
            .password(BuildConfig.HIVEMQ_PASSWORD.toByteArray())
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
