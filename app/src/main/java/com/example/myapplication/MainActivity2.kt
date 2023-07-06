package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity2 : AppCompatActivity() {

    private lateinit var layout1Button: Button
    private lateinit var stickersVerButton: Button

    private val url = "http://192.168.90.206:5000/stickers"

    private fun sendGetRequest() {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                runOnUiThread {
                    if (responseData != null) {
                        val jsonArray = JSONArray(responseData)

                        val mainLayout = findViewById<LinearLayout>(R.id.main_layout)
                        mainLayout.removeAllViews()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val nombre = jsonObject.getString("nombre")
                            val imageUrl = jsonObject.getString("Foto")

                            val linearLayout = LinearLayout(this@MainActivity2)
                            linearLayout.orientation = LinearLayout.VERTICAL

                            val imageButton = ImageButton(this@MainActivity2)
                            Picasso.get().load(imageUrl).into(imageButton)
                            linearLayout.addView(imageButton)

                            val textView = TextView(this@MainActivity2)
                            textView.text = nombre
                            linearLayout.addView(textView)

                            mainLayout.addView(linearLayout)
                        }
                    }
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        layout1Button = findViewById(R.id.go_to_layout1_button)
        stickersVerButton = findViewById(R.id.stickerss_ver_btn) // Corregido el nombre del ID aquí

        layout1Button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        stickersVerButton.setOnClickListener {
            sendGetRequest()
        }
    }
}

