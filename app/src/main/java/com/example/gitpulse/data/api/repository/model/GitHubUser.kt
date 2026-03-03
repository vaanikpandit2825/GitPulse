package com.example.gitpulse.data.model

data class GitHubUser(
    val login: String,
    val avatar_url: String,
    val public_repos: Int,
    val followers: Int,
    val following: Int
)