package com.example.gitpulse

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.gitpulse.data.model.Repo
import com.example.gitpulse.databinding.ActivityProfileBinding
import com.example.gitpulse.viewmodel.GitHubViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: GitHubViewModel
    private var currentUser: com.example.gitpulse.data.model.GitHubUser? = null
    private var totalStars: Int = 0

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

        binding.btnContribution.setOnClickListener {
            val intent = Intent(this, ContributionActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        binding.btnRepos.setOnClickListener {
            val intent = Intent(this, RepositoriesActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        binding.btnDuel.setOnClickListener {
            val intent = Intent(this, DuelInputActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        binding.btnMarketValue.setOnClickListener {
            val intent = Intent(this, MarketValueActivity::class.java).apply {
                putExtra("USERNAME", username)
                putExtra("TOP_LANGUAGE", binding.tvLanguages.text.toString())
                putExtra("TOTAL_STARS", totalStars)
                putExtra("FOLLOWERS", currentUser?.followers ?: 0)
                putExtra("REPOS", currentUser?.public_repos ?: 0)
            }
            startActivity(intent)
        }

        binding.btnShare.setOnClickListener {
            val score = binding.tvDevScore.text.toString()
            val language = binding.tvLanguages.text.toString()
            val prefs = getSharedPreferences("gitpulse", MODE_PRIVATE)
            val streak = prefs.getString("longestStreak", "0") ?: "0"
            val intent = Intent(this, ShareCardActivity::class.java).apply {
                putExtra("USERNAME", username)
                putExtra("STREAK", streak)
                putExtra("LANGUAGE", language)
                putExtra("DEV_SCORE", score)
            }
            startActivity(intent)
        }
    }

    private fun observeData() {
        viewModel.user.observe(this) { user ->
            user?.let {
                currentUser = it
                binding.tvUsername.text = it.login
                binding.tvFollowers.text = it.followers.toString()
                binding.tvFollowing.text = it.following.toString()
                binding.tvRepos.text = it.public_repos.toString()

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

                val topLang = sortedStats.firstOrNull()?.first ?: "—"
                binding.tvLanguages.text = topLang

                totalStars = 0
                it.forEach { repo -> totalStars += repo.stargazers_count }

                val languageCount = languageStats.size
                val user = currentUser ?: return@observe

                val score = calculateDevScore(
                    repos = user.public_repos,
                    followers = user.followers,
                    stars = totalStars,
                    languages = languageCount
                )

                binding.tvDevScore.text = score.toString()

                val rank = when {
                    score >= 500 -> "Gold Tier"
                    score >= 200 -> "Silver Tier"
                    else -> "Bronze Tier"
                }
                binding.tvGlobalRank.text = rank
            }
        }
    }

    private fun calculateDevScore(
        repos: Int,
        followers: Int,
        stars: Int,
        languages: Int
    ): Int {
        return (repos * 2) + (stars * 3) + (languages * 5) + (followers * 1)
    }

    private fun calculateLanguageStats(repos: List<Repo>): Map<String, Int> {
        val languageMap = mutableMapOf<String, Int>()
        for (repo in repos) {
            val language = repo.language ?: continue
            languageMap[language] = languageMap.getOrDefault(language, 0) + 1
        }
        return languageMap
    }
}