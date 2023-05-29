package com.example.edistynytandroidkurssi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.edistynytandroidkurssi.databinding.FragmentChartBinding
import com.example.edistynytandroidkurssi.databinding.FragmentDataBinding
import com.example.edistynytandroidkurssi.datatypes.weatherstation.WeatherStation
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartZoomType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.gson.GsonBuilder
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import java.text.SimpleDateFormat
import java.util.*


class ChartFragment : Fragment() {

    private var _binding: FragmentChartBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.

    // client-olio jolla voidaan yhdistää kiinitettyä MQTT-brokeriin koodin avulla
    private lateinit var client: Mqtt3AsyncClient

    private val binding get() = _binding!!

    // tehdään lista johon kerätään lämpötilat talteen
    val temperatureList = mutableListOf<Double>()
    val humidityList = mutableListOf<Double>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // laitetaan muutama testiarvo, että nähdään toimiiko AAChart oletetusti tällä listalla.
        //temperatureList.add(12.5)
        //temperatureList.add(12.3)
        //temperatureList.add(5.0)
        //temperatureList.add(7.5)


        // temperaturelistin saa AAChartille sopivaan formaattiin, kun käyttää toTypedarray funktiota
        // toimii vain jos temperature list on mutable list --> double
        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .title("Sääasema")
            .subtitle("Rantavitikan mittauspiste")
            .dataLabelsEnabled(true)
            .yAxisMax(150)
            .yAxisMin(-60)
            .zoomType(AAChartZoomType.Y)
            .series(arrayOf(
                AASeriesElement()
                    .name("Lämpötila")
                    .data(temperatureList.toTypedArray()),
                AASeriesElement()
                    .name("Kosteus")
                    .data(humidityList.toTypedArray()),

            )
            )

        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        binding.aaChartView.aa_drawChartWithChartModel(aaChartModel)

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


        fun subscribeToTopic()
        {
        // Alustetaan GSON
        val gson = GsonBuilder().setPrettyPrinting().create()

        client.subscribeWith()
            .topicFilter(BuildConfig.MQTT_TOPIC)
            .callback { publish ->

                // Muutetaan raakadata tekstiksi eli tässä JSONIIN
                var result = String(publish.getPayloadAsBytes())

                //Only refresh the chart series data


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

                    // pidetään huoli, että listan max koko on 10 lämpötilaa
                    while (temperatureList.size > 10) {
                        temperatureList.removeAt(0)
                    }

                    // lisätään uusi lämpötila listaan
                    temperatureList.add(temperature)




                    // pidetään huoli, että listan max koko on 10 lämpötilaa
                    while (humidityList.size > 10) {
                        humidityList.removeAt(0)
                    }

                    // lisätään uusi kosteus listaan
                    humidityList.add(humidity)


                    //tehdään chartin data uusiksi listan pohjalta
                var newArray = arrayOf(
                    AASeriesElement()
                        .name("Lämpötila")
                        .data(temperatureList.toTypedArray()),

                    AASeriesElement()
                        .name("Kosteus")
                        .data(humidityList.toTypedArray()),

                    )



                    // koska MQTT plugin ajaa koodia ja käsittelee dataa
                    // tausta-ajalla omassa säikeessään  eli threadissa
                    // joudumme laittamaan ulkoasuun liittyvän koodin runOnUiThread-blokin
                    // sisälle. Muutoin tulee virhe, että koodit toimivat eri säikeissä
                    activity?.runOnUiThread {

                        binding.aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(newArray, false)

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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null


        // tällä suljetaan MQTT yhteys mikäli fragmen suljetaan.
        client.disconnect()
    }

}

