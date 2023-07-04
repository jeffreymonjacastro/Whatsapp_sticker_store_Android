package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Hace referencia a qué layout se va a usar

        // Un view es un elemento de la interfaz de usuario

        val layout2Button = findViewById<Button>(R.id.go_to_layout2_button)

        // Para cambiar de layout se usa un intent
        layout2Button.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        // Comments
        // lorem ipsun dolor

    }
}