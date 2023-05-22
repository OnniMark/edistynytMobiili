package com.example.edistynytandroidkurssi

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.edistynytandroidkurssi.databinding.FragmentMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

// nyt MapsFragment toteuttaa olio-ohjelmoinnin interfacen
// nimeltä GoogleMap.OnMarketcClickListener ->
// MapsFragmentista pitää nyt löytyä interfacen vaatima metodi eli OnMarkerClick()
class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener {
    
    private var _binding: FragmentMapsBinding? = null
    
    private val binding get() = _binding!!

    // tehdään luokan ylätasolle uusi jäsenmuuttuja
    // tarkoitus tallentaa googleMap-olio talteen, jotta sitä voi
    // käyttää muultakin, kuin callbackin sisältä
    private lateinit var  gMap : GoogleMap
    
    
    private val callback = OnMapReadyCallback { googleMap ->
        // Tämä käynnistyy silloin, kun kartta on valmis ja voidaan käyttää eri ominaisuuksia

        // otetaan jäsenmuuttuja käyttöön.
        gMap = googleMap

        // tehdään marker Sydneylle
        val sydney = LatLng(-34.0, 151.0)
        var  m1 = googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        m1?.tag = "Sydney"


        // Näin meillä on myös jyväskylä kartalla
        // Tehtiin marker Jyväskylälle
        // jos marker tallennetaan muuttujaan, siihen voidaan asettaa lisädataa tagin kautta
        // esim. kaupungin nimi, jos rajapinta ei jostain syystä tukisi koordinaatteja
        val jyväskylä = LatLng(62.24262, 25.74724)
        var m2 = googleMap.addMarker(MarkerOptions().position(jyväskylä).title("Marker in Jyväskylä"))
        m2?.tag = "Jyväskylä"


        //Siirtää mapsin kameran tähän koordinaattiin
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jyväskylä, 17f))


        // Asetetaan, että tämä luokka (this => MapsFragment) huolehtii siitä
        // jos jotain markeria klikataan
        googleMap.setOnMarkerClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // Käytetään binding layeria myös karttafragmentissa
        binding.checkBoxZoomControls.setOnCheckedChangeListener { compoudButton, b ->
            gMap.uiSettings.isZoomControlsEnabled = b
        }

        binding.radioButtonMapNormal.setOnCheckedChangeListener {compundButton, b ->
            //OnChecked käynnistyy molempiin suuntiin, eli jos Radiobutton valitaan
            // Tai valinta poistuu => tulee paljon bugeja , ja Radiobuttonit kilpailevat
            // keskenään mitö koodia pitää totella.
            if(compundButton.isChecked){
                gMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }

        binding.radioButtonMapHybrid.setOnCheckedChangeListener {compundButton, b ->
            //OnChecked käynnistyy molempiin suuntiin, eli jos Radiobutton valitaan
            // Tai valinta poistuu => tulee paljon bugeja , ja Radiobuttonit kilpailevat
            // keskenään mitö koodia pitää totella.
            if(compundButton.isChecked){
                gMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            }
        }

        binding.radioButtonMapTerrain.setOnCheckedChangeListener {compundButton, b ->
            //OnChecked käynnistyy molempiin suuntiin, eli jos Radiobutton valitaan
            // Tai valinta poistuu => tulee paljon bugeja , ja Radiobuttonit kilpailevat
            // keskenään mitö koodia pitää totella.
            if(compundButton.isChecked){
                gMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


        // Tämä metodi vastaa siitä, kun jotain markkeria painetaan kartalla
    override fun onMarkerClick(p0: Marker): Boolean {
            Log.d("TESTI", "marker CLICK!")
            Log.d("TESTI", p0.position.latitude.toString())
            Log.d("TESTI", p0.position.longitude.toString())

            // jos markerilla on tägi niin näin se saadaan näkyuviin
            Log.d("TESTI", p0.tag.toString())

            // Luodaan apumuuttujat ja tallennetaan koordinaatit niihin
            val lat = p0.position.latitude.toFloat()
            val lon = p0.position.longitude.toFloat()

            // actionin avulla siirrytään CityWeatherFragmenttiin ja lähetetään
            // tarvittavat koordinaatit parametreina
            val action = MapsFragmentDirections.actionMapsFragmentToCityWeatherFragment(lat, lon)
            findNavController().navigate(action)
        return false
    }
}