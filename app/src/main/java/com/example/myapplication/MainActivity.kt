package com.example.myapplication

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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
                val url = "http://localhost:5001/login?username=$username&password=$password"
                LoginTask().execute(url)
            } else {
                Toast.makeText(this@MainActivity, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private inner class LoginTask : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            loadingProgressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String): String {
            val url = params[0]

            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStreamReader = InputStreamReader(connection.inputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    val response = bufferedReader.readLine()
                    bufferedReader.close()
                    inputStreamReader.close()
                    return response
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ""
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            loadingProgressBar.visibility = View.GONE

            if (result.isNotEmpty()) {
                val jsonResponse = JSONObject(result)

                if (jsonResponse.has("error")) {
                    val errorMessage = jsonResponse.getString("error")
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this@MainActivity, MainActivity2::class.java)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this@MainActivity, "Failed to connect to the server", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
