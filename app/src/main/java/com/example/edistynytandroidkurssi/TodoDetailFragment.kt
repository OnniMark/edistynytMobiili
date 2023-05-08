


package com.example.edistynytandroidkurssi

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.edistynytandroidkurssi.databinding.FragmentDetailBinding
import com.example.edistynytandroidkurssi.databinding.FragmentTodoBinding
import com.example.edistynytandroidkurssi.databinding.FragmentTodoDetailBinding
import com.google.gson.GsonBuilder


class TodoDetailFragment : Fragment() {
    private var _binding: FragmentTodoDetailBinding? = null
    private val binding get() = _binding!!

    // Get fragment parameters from previous fragment
    private val args: TodoDetailFragmentArgs by navArgs()

    // Gson instance for JSON parsing
    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodoDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Print out the given parameter into logs
        Log.d("TESTI", "Todon id-parametri: ${args.id}")

        val JSON_URL = "https://jsonplaceholder.typicode.com/todos/${args.id}"
        Log.d("TESTI", JSON_URL)

        // Create a Volley request queue
        val queue = Volley.newRequestQueue(requireContext())

        // Create a GET request for the JSON data
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, JSON_URL, null,
            { response ->
                // Parse the JSON response into a Todo object using Gson
                val item: Todo = gson.fromJson(response.toString(), Todo::class.java)

                // Update the UI with the retrieved data
                binding.TextViewTitle.text = item.title
                binding.textViewTodoId.text = item.id.toString()
                binding.textViewTodoUserId.text = item.userId.toString()
                binding.textViewTodoCompleted.text = item.completed.toString()

                if (item.completed == true) {
                    binding.textViewTodoCompleted.text = "✔"
                    binding.textViewTodoCompleted.setTextColor(Color.GREEN)
                } else {
                    binding.textViewTodoCompleted.text = "X"
                    binding.textViewTodoCompleted.setTextColor(Color.RED)
                }
            },
            { error ->
                // Handle error cases here
                Log.e("TESTI", "Error: ${error.message}")
            }
        )

        // Add the request to the request queue
        queue.add(jsonObjectRequest)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
