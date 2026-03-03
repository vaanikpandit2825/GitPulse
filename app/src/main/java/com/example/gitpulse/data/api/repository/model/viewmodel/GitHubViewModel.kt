package com.example.gitpulse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitpulse.data.model.GitHubUser
import com.example.gitpulse.data.repository.GitHubRepository
import kotlinx.coroutines.launch

class GitHubViewModel : ViewModel() {

    private val repository = GitHubRepository()

    private val _user = MutableLiveData<GitHubUser?>()
    val user: LiveData<GitHubUser?> = _user

    fun fetchUser(username: String) {
        viewModelScope.launch {
            try {
                val response = repository.getUser(username)

                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {
                    _user.value = null
                }

            } catch (e: Exception) {
                _user.value = null
            }
        }
    }
}