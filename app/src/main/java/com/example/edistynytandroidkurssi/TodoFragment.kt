package com.example.edistynytandroidkurssi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.edistynytandroidkurssi.databinding.FragmentDataBinding
import com.example.edistynytandroidkurssi.databinding.FragmentTodoBinding
import com.google.gson.GsonBuilder


/**
 * A simple [Fragment] subclass.
 * Use the [TodoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TodoFragment : Fragment() {
    private var _binding: FragmentTodoBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    // alustetaan viittaus adapteriin sekä luodaan LinearLayoutManager
// RecyclerView tarvitsee jonkin LayoutManagerin, joista yksinkertaisin on Linear
    private lateinit var adapter: TodoAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // nappia painamalla hakee datan
        binding.buttonGetTodos.setOnClickListener {
            getDotos()
        }

        // Luodaan linear layout manager ja kytketään se ulkoasussa olevaan Recyclervieweriin
        // Tarkista, että recyclerviewerin id: täsmää binding koodin kanssa
        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = linearLayoutManager

        return root
    }



    // tällä haetaan dataa rajapinnasta
    fun getDotos(){
        Log.d("TESTI","GetTodos() kutsuttu")

        // this is the url where we want to get our data from
        val JSON_URL = "https://jsonplaceholder.typicode.com/todos"

        // tällä haetaan GSON-objekti käyttöön
        val gson = GsonBuilder().setPrettyPrinting().create()

        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, JSON_URL,
            Response.Listener { response ->

                // print the response as a whole
                // we can use GSON to modify this response into something more usable
                Log.d("TESTI", response)

                var rows : List<Todo> = gson.fromJson(response, Array<Todo>::class.java).toList()


                // luodaan adapteri omalla datalla ja kytketään se recyclervieweriin.
                adapter = TodoAdapter(rows)
                binding.recyclerView.adapter = adapter


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
