package com.example.gitpulse

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.gitpulse.data.api.RetrofitInstance
import com.example.gitpulse.data.model.GitHubUser
import com.example.gitpulse.data.model.Repo
import com.example.gitpulse.databinding.ActivityDuelBinding
import com.example.gitpulse.databinding.ItemDuelRowBinding
import kotlinx.coroutines.launch

class DuelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDuelBinding
    private var userA: GitHubUser? = null
    private var userB: GitHubUser? = null

    private var reposA: List<Repo> = emptyList()
    private var reposB: List<Repo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDuelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usernameA = intent.getStringExtra("USERNAME_A") ?: ""
        val usernameB = intent.getStringExtra("USERNAME_B") ?: ""

        Log.d("DUEL_DEBUG", "A: $usernameA, B: $usernameB")

        binding.btnBack.setOnClickListener { finish() }

        fetchUsers(usernameA, usernameB)
    }

    private fun fetchUsers(usernameA: String, usernameB: String) {
        lifecycleScope.launch {
            try {
                val responseA = RetrofitInstance.api.getUser(usernameA)
                val responseB = RetrofitInstance.api.getUser(usernameB)

                val repoResponseA = RetrofitInstance.api.getRepos(usernameA)
                val repoResponseB = RetrofitInstance.api.getRepos(usernameB)

                if (responseA.isSuccessful) userA = responseA.body()
                if (responseB.isSuccessful) userB = responseB.body()

                if (repoResponseA.isSuccessful) reposA = repoResponseA.body() ?: emptyList()
                if (repoResponseB.isSuccessful) reposB = repoResponseB.body() ?: emptyList()

                displayDuel()

            } catch (e: Exception) {
                Log.e("DUEL_ERROR", e.message.toString())
            }
        }
    }

    private fun getTopLanguage(repos: List<Repo>): String {
        val freq = mutableMapOf<String, Int>()

        for (repo in repos) {
            val lang = repo.language ?: continue
            freq[lang] = freq.getOrDefault(lang, 0) + 1
        }

        return freq.maxByOrNull { it.value }?.key ?: "N/A"
    }

    private fun displayDuel() {
        val a = userA ?: return
        val b = userB ?: return

        binding.tvUsernameA.text = "@${a.login}"
        binding.tvUsernameB.text = "@${b.login}"

        Glide.with(this).load(a.avatar_url).circleCrop().into(binding.imgAvatarA)
        Glide.with(this).load(b.avatar_url).circleCrop().into(binding.imgAvatarB)

        val prefs = getSharedPreferences("gitpulse", MODE_PRIVATE)
        val streakA = prefs.getString("longestStreak", "0")?.toIntOrNull() ?: 0
        val streakB = 0

        val scoreA = ScoreUtils.calculateScore(a, reposA)
        val scoreB = ScoreUtils.calculateScore(b, reposB)

        val topLangA = getTopLanguage(reposA)
        val topLangB = getTopLanguage(reposB)

        setupRow(
            binding.rowFollowers,
            "Followers",
            a.followers.toString(),
            b.followers.toString(),
            a.followers > b.followers
        )
        setupRow(
            binding.rowFollowing,
            "Following",
            a.following.toString(),
            b.following.toString(),
            a.following > b.following
        )
        setupRow(
            binding.rowRepos,
            "Repositories",
            a.public_repos.toString(),
            b.public_repos.toString(),
            a.public_repos > b.public_repos
        )
        setupRow(
            binding.rowStreak,
            "Longest Streak",
            "$streakA days",
            "$streakB days",
            streakA > streakB
        )
        setupRow(binding.rowLanguage, "Top Language", topLangA, topLangB, topLangA == topLangB)
        setupRow(
            binding.rowDevScore,
            "Dev Score",
            scoreA.toString(),
            scoreB.toString(),
            scoreA > scoreB
        )

        if (scoreA > scoreB) {
            binding.tvWinner.text = "Winner: @${a.login} 🏆"
            binding.tvWinnerTagline.text = "Dominating in consistency!"
        } else if (scoreB > scoreA) {
            binding.tvWinner.text = "Winner: @${b.login} 🏆"
            binding.tvWinnerTagline.text = "Dominating in consistency!"
        } else {
            binding.tvWinner.text = "It's a Tie! 🤝"
            binding.tvWinnerTagline.text = "Both developers are equally matched!"
        }
    }

    private fun setupRow(
        rowBinding: ItemDuelRowBinding,
        label: String,
        valueA: String,
        valueB: String,
        aWins: Boolean
    ) {
        rowBinding.tvLabel.text = label
        rowBinding.tvValueA.text = valueA
        rowBinding.tvValueB.text = valueB

        if (aWins) {
            rowBinding.tvValueA.setTextColor(Color.parseColor("#5B8AFF"))
            rowBinding.tvValueB.setTextColor(Color.parseColor("#8B949E"))
        } else {
            rowBinding.tvValueA.setTextColor(Color.parseColor("#8B949E"))
            rowBinding.tvValueB.setTextColor(Color.parseColor("#5B8AFF"))
        }
    }
}

