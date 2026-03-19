package com.example.gitpulse

import com.example.gitpulse.data.model.GitHubUser
import com.example.gitpulse.data.model.Repo


object ScoreUtils{
    fun calculateScore(user: GitHubUser,repos: List<Repo>): Int{
        val repoCount = user.public_repos
        val followers = user.followers

        val totalStars = repos.sumOf { repo -> repo.stargazers_count }
        val languages = repos.mapNotNull { repo -> repo.language }.toSet().size

        return (repoCount * 2) +
                (totalStars * 3) +
                (languages * 5) +
                (followers * 1)

    }
}