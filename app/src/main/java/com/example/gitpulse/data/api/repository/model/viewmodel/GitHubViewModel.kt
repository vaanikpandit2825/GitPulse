package com.example.gitpulse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitpulse.data.model.GitHubUser
import com.example.gitpulse.data.model.Repo
import com.example.gitpulse.data.repository.GitHubRepository
import kotlinx.coroutines.launch

class GitHubViewModel : ViewModel() {

    private val repository = GitHubRepository()

    private val _user = MutableLiveData<GitHubUser>()
    val user: LiveData<GitHubUser> = _user

    private val _repos = MutableLiveData<List<Repo>>()
    val repos: LiveData<List<Repo>> = _repos


    fun fetchUser(username: String) {

        viewModelScope.launch {

            try {
                val response = repository.getUser(username)

                if (response.isSuccessful) {
                    _user.value = response.body()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    fun fetchRepos(username: String) {

        viewModelScope.launch {

            try {
                val response = repository.getRepos(username)

                if (response.isSuccessful) {
                    _repos.value = response.body()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
