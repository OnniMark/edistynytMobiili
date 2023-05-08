package com.example.edistynytandroidkurssi

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.edistynytandroidkurssi.databinding.RecyclerViewItemBinding

class TodoAdapter (private val Todo: List<Todo>) : RecyclerView.Adapter<TodoAdapter.TodoHolder>() {

    // binding layerin muuttujien alustaminen
    private var _binding: RecyclerViewItemBinding? = null
    private val binding get() = _binding!!


    // ViewHolderin onCreate-metodi. käytännössä tässä kytketään binding layer
    // osaksi CommentHolder-luokkaan (adapterin sisäinen luokka)
    // koska CommentAdapter pohjautuu RecyclerViewin perusadapteriin, täytyy tästä
    // luokasta löytyä metodi nimeltä onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoHolder {
        // binding layerina toimii yksitätinen recyclerview_item_row.xml -instanssi
        _binding = RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoAdapter.TodoHolder(binding)
    }

    // tämä metodi kytkee yksittäisen Comment-objektin yksittäisen CommentHolder-instanssiin
    // koska CommentAdapter pohjautuu RecyclerViewin perusadapteriin, täytyy tästä
    // luokasta löytyä metodi nimeltä onBindViewHolder
    override fun onBindViewHolder(holder: TodoHolder, position: Int) {
        val itemTodo = Todo[position]
        holder.bindTodo(itemTodo)
    }

    // Adapterin täytyy pysty tietämään sisältämänsä datan koko tämän metodin avulla
    // koska CommentAdapter pohjautuu RecyclerViewin perusadapteriin, täytyy tästä
    // luokasta löytyä metodi nimeltä getItemCount
    override fun getItemCount(): Int {
        return Todo.size
    }




    // CommentHolder, joka määritettiin oman CommentAdapterin perusmäärityksessä (ks. luokan yläosa)
    // Holder-luokka sisältää logiikan, jolla data ja ulkoasu kytketään toisiinsa
    class TodoHolder(v: RecyclerViewItemBinding) : RecyclerView.ViewHolder(v.root), View.OnClickListener {

        // tämän kommentin ulkoasu ja varsinainen data
        private var view: RecyclerViewItemBinding = v
        private var Todo: Todo? = null

        // mahdollistetaan yksittäisen itemin klikkaaminen tässä luokassa
        init {
            // Tämä mahdollistaa sen ,kun Todota klitataan
            //suoritetaan alla tapahtyva OnClick
            v.root.setOnClickListener(this)

        }

        // metodi, joka kytkee datan yksityiskohdat ulkoasun yksityiskohtiin
        fun bindTodo(Todo: Todo) {
            this.Todo = Todo


            view.textViewTodoTitle.text = Todo.title




            }

            // jos itemiä klikataan käyttöliittymässä, ajetaan tämä koodio
            override fun onClick(v: View) {
                Log.d("TESTI", "Recycler-Todo -klikattu!")
                Log.d("TESTI", " Titlen ID =" + Todo?.id.toString())

                // Ha
                val action =
                    TodoFragmentDirections.actionTodoFragmentToTodoDetailFragment(Todo?.id as Int)
                v.findNavController().navigate(action)

            }
        }
    }





