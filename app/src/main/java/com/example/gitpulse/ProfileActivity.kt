package com.example.gitpulse

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        val username=intent.getStringExtra("USERNAME")

        if(username!=null){
            Toast.makeText(this,"Username: $username", Toast.LENGTH_SHORT).show()
        }
    }
}