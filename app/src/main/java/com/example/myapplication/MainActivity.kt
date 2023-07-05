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
import java.util.concurrent.TimeUnit
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private val url = "http://10.100.238.101:5000/login"

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
                // Manejar el error de conexión aquí
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

                            val linearLayout = LinearLayout(this@MainActivity)
                            linearLayout.orientation = LinearLayout.VERTICAL

                            val imageButton = ImageButton(this@MainActivity)
                            Picasso.get().load(imageUrl).into(imageButton)
                            linearLayout.addView(imageButton)

                            val textView = TextView(this@MainActivity)
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
        setContentView(R.layout.activity_main)

        loginButton = findViewById(R.id.go_to_layout2_button)
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Verificar el usuario en la base de datos antes de enviar la solicitud GET
            if (validateUser(username, password)) {
                sendGetRequest()
            } else {
                // Manejar la autenticación fallida aquí
            }
        }

        val layout2Button = findViewById<Button>(R.id.go_to_layout2_button)
        layout2Button.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }

    private fun validateUser(username: String, password: String): Boolean {
        val client = OkHttpClient()

        val url = "http://localhost:5001/login" // Cambia la URL por la de tu API

        val json = JSONObject()
        json.put("username", username)
        json.put("password", password)

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Manejar el caso de error de conexión
                println("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                if (response.isSuccessful && responseData != null) {
                    val jsonResponse = JSONObject(responseData)

                    // Verificar si la autenticación fue exitosa
                    val authenticated = jsonResponse.getBoolean("authenticated")

                    if (authenticated) {
                        println("Autenticación exitosa")
                        // Realizar las acciones necesarias después de la autenticación exitosa
                    } else {
                        println("Autenticación fallida")
                        // Realizar las acciones necesarias después de la autenticación fallida
                    }
                } else {
                    // Manejar el caso de respuesta no exitosa
                    println("Respuesta no exitosa: ${response.code}")
                }
            }
        })

        // Devolver un valor temporal mientras se realiza la llamada asíncrona
        return true
    }
}
