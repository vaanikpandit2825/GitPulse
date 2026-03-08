package com.example.gitpulse

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gitpulse.data.model.Repo
import com.example.gitpulse.databinding.ItemRepoBinding

class RepoAdapter(
    private val repos: List<Repo>
) : RecyclerView.Adapter<RepoAdapter.RepoViewHolder>() {

    class RepoViewHolder(val binding: ItemRepoBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {

        val binding = ItemRepoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RepoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return repos.size
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {

        val repo = repos[position]

        holder.binding.tvRepoName.text = repo.name

        val language = repo.language ?: "Unknown"

        holder.binding.tvRepoDetails.text =
            "⭐ ${repo.stargazers_count}   $language"
    }
}