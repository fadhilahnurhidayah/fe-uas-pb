package com.example.kantinsekre.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kantinsekre.models.User

class SharedViewModel : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    fun getCurrentUser(): User? {
        return _currentUser.value
    }

    fun clearCurrentUser() {
        _currentUser.value = null
    }

    fun isUserLoggedIn(): Boolean {
        return _currentUser.value != null
    }
}