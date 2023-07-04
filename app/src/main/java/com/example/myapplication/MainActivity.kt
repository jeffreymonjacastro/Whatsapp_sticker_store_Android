package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var login : Button
    private lateinit var editText : EditText
    private lateinit var username : String
    private lateinit var password : String
    private val url = "http://10.100.238.101:5000/login"

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
        setContentView(R.layout.activity_main) // Hace referencia a qué layout se va a usar

        // Un view es un elemento de la interfaz de usuario

        val layout2Button = findViewById<Button>(R.id.go_to_layout2_button)

        editText = findViewById(R.id.)

        // Para cambiar de layout se usa un intent
        layout2Button.setOnClickListener {


            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        // Comments
        // lorem ipsun dolor

    }
}