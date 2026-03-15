package com.example.gitpulse

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gitpulse.data.model.Repo
import com.example.gitpulse.databinding.ItemRepoBinding

class RepoAdapter(
    private val repos: List<Repo>
) : RecyclerView.Adapter<RepoAdapter.RepoViewHolder>() {

    class RepoViewHolder(val binding: ItemRepoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = ItemRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoViewHolder(binding)
    }

    override fun getItemCount() = repos.size

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repo = repos[position]

        holder.binding.tvRepoName.text = repo.name
        holder.binding.tvStars.text = "⭐ ${repo.stargazers_count}"
        holder.binding.tvRepoDetails.text = repo.description ?: "No description"

        val language = repo.language ?: "Unknown"
        holder.binding.tvLanguage.text = language

        val langColor = when (language) {
            "Kotlin" -> "#A97BFF"
            "Java" -> "#B07219"
            "Python" -> "#3572A5"
            "JavaScript" -> "#F1E05A"
            "TypeScript" -> "#2B7489"
            "Swift" -> "#FA7343"
            "Dart" -> "#00B4AB"
            "C++" -> "#F34B7D"
            "C#" -> "#178600"
            "Go" -> "#00ADD8"
            "Rust" -> "#DEA584"
            "HTML" -> "#E34C26"
            "CSS" -> "#563D7C"
            else -> "#8B949E"
        }

        val drawable = holder.binding.viewLangDot.background.mutate()
        (drawable as android.graphics.drawable.GradientDrawable).setColor(Color.parseColor(langColor))

        holder.binding.tvRepoHealth.text = "Updated: ${repo.updated_at.substring(0, 10)}"
    }
}