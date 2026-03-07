package com.example.gitpulse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.gitpulse.data.model.GitHubUser
import com.example.gitpulse.data.model.Repo
import com.example.gitpulse.databinding.ActivityProfileBinding
import com.example.gitpulse.viewmodel.GitHubViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: GitHubViewModel

    private var currentUser: GitHubUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[GitHubViewModel::class.java]

        val username = intent.getStringExtra("USERNAME")

        if (username == null) {
            binding.tvUsername.text = "Unknown User"
            return
        }

        binding.tvUsername.text = username

        viewModel.fetchUser(username)
        viewModel.fetchRepos(username)

        observeData()
    }

    private fun observeData() {

        viewModel.user.observe(this) { user ->

            user?.let {

                currentUser = it

                binding.tvUsername.text = it.login
                binding.tvFollowers.text = "Followers: ${it.followers}"
                binding.tvFollowing.text = "Following: ${it.following}"
                binding.tvRepos.text = "Repos: ${it.public_repos}"

                Glide.with(this)
                    .load(it.avatar_url)
                    .circleCrop()
                    .into(binding.imgAvatar)
            }
        }

        viewModel.repos.observe(this) { repos ->

            repos?.let {

                val languageStats = calculateLanguageStats(it)

                val sortedStats = languageStats.toList()
                    .sortedByDescending { entry -> entry.second }

                val statsText = sortedStats.joinToString("\n") { entry ->
                    "${entry.first} : ${entry.second}"
                }

                binding.tvLanguages.text = statsText

                var totalStars = 0
                it.forEach { repo ->
                    totalStars += repo.stargazers_count
                }

                val languageCount = languageStats.size

                val user = currentUser ?: return@observe

                val score = calculateDevScore(
                    repos = user.public_repos,
                    followers = user.followers,
                    stars = totalStars,
                    languages = languageCount
                )

                binding.tvDevScore.text = "Dev Score: $score"
            }
        }
    }

    private fun calculateDevScore(
        repos: Int,
        followers: Int,
        stars: Int,
        languages: Int
    ): Int {

        return (repos * 2) +
                (stars * 3) +
                (languages * 5) +
                (followers * 1)
    }

    private fun calculateLanguageStats(repos: List<Repo>): Map<String, Int> {

        val languageMap = mutableMapOf<String, Int>()

        for (repo in repos) {

            val language = repo.language ?: continue

            languageMap[language] =
                languageMap.getOrDefault(language, 0) + 1
        }

        return languageMap
    }
}