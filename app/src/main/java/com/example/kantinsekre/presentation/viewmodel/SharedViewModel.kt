package com.example.kantinsekre.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kantinsekre.models.UserItem
import com.example.kantinsekre.presentation.state.UiState

class SharedViewModel : ViewModel() {
    private val _currentUser = MutableLiveData<UserItem?>()
    val currentUser: LiveData<UserItem?> = _currentUser

    private val _isLoading = MutableLiveData<Boolean>()

    private var authViewModel: AuthViewModel? = null

    fun initialize(authViewModel: AuthViewModel) {
        this.authViewModel = authViewModel

        authViewModel.currentUser.observeForever { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    _isLoading.value = true
                }
                is UiState.Success -> {
                    _isLoading.value = false
                    val displayUser = uiState.data
                    val userItem = UserItem(
                        idUser = displayUser.idUser,
                        nama = displayUser.nama,
                        passwordHash = null,
                        role = displayUser.role
                    )
                    _currentUser.value = userItem
                }
                is UiState.Error -> {
                    _isLoading.value = false
                }
                is UiState.Idle -> {
                    _isLoading.value = false
                }
            }
        }

        loadCurrentUser()
    }

    fun loadCurrentUser() {
        authViewModel?.fetchCurrentUser()
    }

    fun getCurrentUser(): UserItem? {
        return _currentUser.value
    }
}