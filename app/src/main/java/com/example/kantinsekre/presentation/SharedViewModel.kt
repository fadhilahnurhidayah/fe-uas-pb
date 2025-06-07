package com.example.kantinsekre.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kantinsekre.models.User

/**
 * ViewModel yang digunakan untuk berbagi data antar komponen UI
 * Terutama untuk manajemen state user yang sedang login
 */
class SharedViewModel : ViewModel() {
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    /**
     * Mengatur user yang sedang login
     * @param user User yang akan diset sebagai current user
     */
    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    /**
     * Mendapatkan user yang sedang login
     * @return User yang sedang login atau null jika tidak ada user yang login
     */
    fun getCurrentUser(): User? = _currentUser.value

    /**
     * Menghapus data user yang sedang login
     * Biasanya dipanggil saat logout
     */
    fun clearCurrentUser() {
        _currentUser.value = null
    }

    /**
     * Mengecek apakah ada user yang sedang login
     * @return true jika ada user yang login, false jika tidak
     */
    fun isUserLoggedIn(): Boolean = _currentUser.value != null
}