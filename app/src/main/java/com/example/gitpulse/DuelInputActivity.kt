package com.example.gitpulse

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.gitpulse.databinding.ActivityDuelInputBinding

class DuelInputActivity : AppCompatActivity(){
     private lateinit var binding: ActivityDuelInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDuelInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usernameA = intent.getStringExtra("USERNAME")?: ""
        binding.btnBack.setOnClickListener{finish()}

        binding.btnStartDuel.setOnClickListener {
            val usernameB = binding.etOpponent.text.toString().trim()
            if(usernameB.isEmpty()){
                binding.etOpponent.error = "Enter a username"
                return@setOnClickListener
            }
            val intent = Intent(this, DuelActivity::class.java).apply{
                putExtra("USERNAME_A",usernameA)
                putExtra("USERNAME_B",usernameB)
            }
            startActivity(intent)
        }
    }
}