package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import okhttp3.RequestBody

class MainActivity : AppCompatActivity() {

        private lateinit var loginButton: Button
        private lateinit var usernameEditText: EditText
        private lateinit var passwordEditText: EditText

        val temp_string = "application/json; charset=utf-8"
        private val temp_media_type = temp_string.toMediaTypeOrNull()
        private val okHttpClient = OkHttpClient()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            loginButton = findViewById(R.id.go_to_layout2_button)
            usernameEditText = findViewById(R.id.username)
            passwordEditText = findViewById(R.id.password)

            loginButton.setOnClickListener {
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                val url = HttpUrl.Builder()
                    .scheme("http")
                    .host("10.100.230.205:5000")
                    .addPathSegment("login")
                    .addQueryParameter("username", username)
                    .addQueryParameter("password", password)
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()

                        if (response.isSuccessful && responseBody != null) {
                            val persona = JSONObject(responseBody)

                            runOnUiThread {
                                val success = persona.getBoolean("success")
                                if (success) {
                                    // Login successful
                                } else {
                                    val errorMessage = persona.getString("error_message")
                                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error en la solicitud",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Error en la conexión",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            }

            val layout2Button = findViewById<Button>(R.id.go_to_layout2_button)
            layout2Button.setOnClickListener {
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            }
        }

        private fun validateUser(username: String, password: String): Boolean {
            val client = OkHttpClient()

            val url = "http://localhost:5001/login"

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
