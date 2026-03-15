package com.example.gitpulse

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gitpulse.data.model.Repo
import com.example.gitpulse.databinding.ActivityRepositoriesBinding
import com.example.gitpulse.viewmodel.GitHubViewModel

class RepositoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepositoriesBinding
    private lateinit var viewModel: GitHubViewModel
    private var allRepos: List<Repo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepositoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[GitHubViewModel::class.java]

        val username = intent.getStringExtra("USERNAME")

        binding.btnBack.setOnClickListener { finish() }

        binding.recyclerRepos.layoutManager = LinearLayoutManager(this)

        if (username != null) {
            viewModel.fetchRepos(username)
        }

        viewModel.repos.observe(this) { repos ->
            repos?.let {
                allRepos = it
                binding.recyclerRepos.adapter = RepoAdapter(it)
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filtered = allRepos.filter {
                    it.name.lowercase().contains(query) ||
                            (it.language?.lowercase()?.contains(query) == true)
                }
                binding.recyclerRepos.adapter = RepoAdapter(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}