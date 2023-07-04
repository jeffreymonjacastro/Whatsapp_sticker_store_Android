package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.picasso.Picasso
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException
import java.util.concurrent.TimeUnit


class MainActivity2 : AppCompatActivity() {

    // Variables
    private lateinit var stickers_ver_btn : Button
    private lateinit var sticker_ver_btn : ImageButton
    private lateinit var sticker_text : TextView
    private val url = "http://10.0.2.2:6000/stickers"


    private fun sendGetRquest() {
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

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val nombre = jsonObject.getString("nombre") // Aquí poner las llaves del json de respuesta
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

        val layout1Button = findViewById<Button>(R.id.go_to_layout1_button)

        layout1Button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Aquí vamos a hacer el fectch [GET]
        stickers_ver_btn = findViewById(R.id.stickerss_ver_btn)
//        sticker_ver_btn = findViewById(R.id.sticker_ver_btn)
//        sticker_text = findViewById(R.id.sticker_text)

        // Request de GET
        stickers_ver_btn.setOnClickListener {
            sendGetRquest()
        }
    }
}