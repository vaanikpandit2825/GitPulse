package com.example.gitpulse.data.model

data class Repo(
    val name: String,
    val description: String?,
    val language: String?,
    val stargazers_count: Int,
    val forks_count: Int
)