package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.go_to_layout2_button)
        loadingProgressBar = findViewById(R.id.loading)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val url = "http://localhost:5001/login"
                val parameters = JSONObject()
                parameters.put("username", username)
                parameters.put("password", password)

                login(url, parameters.toString())
            } else {
                Toast.makeText(this@MainActivity, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(url: String, jsonBody: String) {
        val client = OkHttpClient()

        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        loadingProgressBar.visibility = View.VISIBLE

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, "Failed to connect to the server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val jsonData = response.body?.string()
                        jsonData?.let {
                            val jsonResponse = JSONObject(jsonData)

                            if (jsonResponse.has("error")) {
                                val errorMessage = jsonResponse.getString("error")
                                runOnUiThread {
                                    loadingProgressBar.visibility = View.GONE
                                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                runOnUiThread {
                                    loadingProgressBar.visibility = View.GONE
                                    val intent = Intent(this@MainActivity, MainActivity2::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    } else {
                        runOnUiThread {
                            loadingProgressBar.visibility = View.GONE
                            Toast.makeText(this@MainActivity, "Failed to connect to the server", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}

