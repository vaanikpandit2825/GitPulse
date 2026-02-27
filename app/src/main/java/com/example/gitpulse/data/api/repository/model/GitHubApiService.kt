package com.example.gitpulse.data.api


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import com.example.gitpulse.data.model.GitHubUser
import com.example.gitpulse.data.model.Repo



interface GitHubApiService {

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): Response<GitHubUser>

    @GET("users/{username}/repos")
    suspend fun getRepos(
        @Path("username") username: String
    ): Response<List<Repo>>
}