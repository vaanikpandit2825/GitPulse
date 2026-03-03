package com.example.gitpulse.data.repository

import com.example.gitpulse.data.api.RetrofitInstance

class GitHubRepository {

    suspend fun getUser(username: String) =
        RetrofitInstance.api.getUser(username)

    suspend fun getRepos(username: String) =
        RetrofitInstance.api.getRepos(username)
}