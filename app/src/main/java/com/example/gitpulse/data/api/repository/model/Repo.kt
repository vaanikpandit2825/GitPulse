package com.example.gitpulse.data.model

data class Repo(
    val name: String,
    val description: String?,
    val language: String?,
    val stargazers_count: Int,
    val forks_count: Int,
    val updated_at: String,
    val license: License?
)

data class License(
    val name: String?
)