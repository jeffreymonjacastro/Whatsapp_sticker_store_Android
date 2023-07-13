package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONArray
import java.io.IOException


class MainActivity2 : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StickerAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val imageUrls = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        recyclerView = findViewById(R.id.image_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StickerAdapter(imageUrls)
        recyclerView.adapter = adapter

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            fetchStickers()
        }

        fetchStickers()
    }

    private fun fetchStickers() {
        val url = "http://localhost:5001/stickers"

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Manejo del error de la solicitud
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        // Manejo del error de respuesta
                        return
                    }

                    val jsonData = response.body?.string()

                    jsonData?.let {
                        parseStickers(jsonData)
                    }
                }
            }
        })
    }

    private fun parseStickers(jsonData: String) {
        try {
            val jsonArray = JSONArray(jsonData)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val foto = jsonObject.getString("Foto")
                imageUrls.add(foto)
            }

            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class StickerAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<StickerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_main2, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val imageUrl = imageUrls[position]
            Picasso.get().load(imageUrl).into(holder.imageView)
        }

        override fun getItemCount(): Int {
            return imageUrls.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.image_recycler_view)
        }
    }
}