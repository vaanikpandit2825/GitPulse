package com.example.gitpulse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.gitpulse.data.model.Repo
import com.example.gitpulse.databinding.ActivityProfileBinding
import com.example.gitpulse.viewmodel.GitHubViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.view.View
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: GitHubViewModel
    private var currentUser: com.example.gitpulse.data.model.GitHubUser? = null

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

        binding.btnShare.setOnClickListener {
            shareCard()
        }
        binding.btnRepos.setOnClickListener {
            val intent = Intent(this, RepositoriesActivity::class.java)
            intent.putExtra("USERNAME", username)
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

                var totalStars = 0
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

    private fun shareCard() {
        val card = ShareCardView(this)
        card.username = viewModel.user.value?.login ?: ""
        card.longestStreak = "14"
        card.topLanguage = viewModel.repos.value
            ?.groupBy { it.language ?: "" }
            ?.filterKeys { it.isNotEmpty() }
            ?.maxByOrNull { it.value.size }
            ?.key ?: "Unknown"
        card.currentVibe = Vibe.values().random()

        card.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )
        card.layout(0, 0, 1080, 1920)

        val bitmap = card.toBitmap()
        val file = File(cacheDir, "sharecard.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()

        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "Share your GitPulse card!"))
    }
}
