package com.example.gitpulse

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.gitpulse.databinding.ActivityMainBinding
import com.example.gitpulse.viewmodel.GitHubViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: GitHubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFetch.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()

            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            } else {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnFetch.isEnabled = false
                viewModel.fetchUser(username)
            }
        }

        viewModel.user.observe(this) { user: com.example.gitpulse.data.model.GitHubUser? ->

            binding.progressBar.visibility = View.GONE
            binding.btnFetch.isEnabled = true

            if (user != null) {
                Toast.makeText(this, "User found: ${user.login}", Toast.LENGTH_SHORT).show()

                // TODO: Navigate to Profile screen later
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}