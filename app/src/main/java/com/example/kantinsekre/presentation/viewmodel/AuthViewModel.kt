package com.example.kantinsekre.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kantinsekre.models.AuthResponse
import com.example.kantinsekre.models.DisplayUser
import com.example.kantinsekre.models.User
import com.example.kantinsekre.models.toDisplayUser
import com.example.kantinsekre.models.toDisplayUserList
import com.example.kantinsekre.presentation.state.UiState
import com.example.kantinsekre.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<UiState<AuthResponse>>()
    val loginResult: LiveData<UiState<AuthResponse>> = _loginResult

    private val _users = MutableLiveData<UiState<List<DisplayUser>>>()
    val users: LiveData<UiState<List<DisplayUser>>> = _users

    private val _currentUser = MutableLiveData<UiState<DisplayUser>>()
    val currentUser: LiveData<UiState<DisplayUser>> = _currentUser

    private val _addUserResult = MutableLiveData<UiState<Unit>>()
    val addUserResult: LiveData<UiState<Unit>> = _addUserResult

    private val _updateUserResult = MutableLiveData<UiState<Unit>>()
    val updateUserResult: LiveData<UiState<Unit>> = _updateUserResult

    private val _deleteUserResult = MutableLiveData<UiState<Unit>>()
    val deleteUserResult: LiveData<UiState<Unit>> = _deleteUserResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = UiState.Loading
            try {
                val user = User(username, password)
                val response = repository.login(user)
                if (response.isSuccessful) {
                    response.body()?.let { authResponse ->
                        if (authResponse.success == true) {
                            _loginResult.value = UiState.Success(authResponse)
                        } else {
                            _loginResult.value = UiState.Error(authResponse.message ?: "Login gagal")
                        }
                    } ?: run {
                        _loginResult.value = UiState.Error("Response login kosong")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Username atau password salah"
                        404 -> "User tidak ditemukan"
                        500 -> "Server error, coba lagi nanti"
                        else -> "Login gagal: ${response.message()}"
                    }
                    _loginResult.value = UiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _loginResult.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            _users.value = UiState.Loading
            try {
                val response = repository.getAllUsers()
                if (response.isSuccessful && response.body()?.success == true) {
                    val userItemList = response.body()?.data ?: emptyList()
                    val displayUserList = userItemList.toDisplayUserList()
                    _users.value = UiState.Success(displayUserList)
                } else {
                    val errorMessage = response.body()?.message ?: "Gagal memuat daftar user"
                    _users.value = UiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _users.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun fetchCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = UiState.Loading
            try {
                val response = repository.getCurrentUser()
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true) {
                        val currentUserData = responseBody.data
                        if (currentUserData != null) {
                            val displayUser = currentUserData.toDisplayUser()
                            _currentUser.value = UiState.Success(displayUser)
                            Log.d("AuthViewModel", "✅ Current user berhasil dimuat: ${displayUser.nama} (Role: ${displayUser.role})")
                        } else {
                            val errorMsg = "Data current user kosong"
                            Log.e("AuthViewModel", "❌ $errorMsg")
                            _currentUser.value = UiState.Error(errorMsg)
                        }
                    } else {
                        val errorMsg = responseBody?.message ?: "Response tidak berhasil"
                        Log.e("AuthViewModel", "❌ API Error: $errorMsg")
                        _currentUser.value = UiState.Error(errorMsg)
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Token tidak valid atau sudah expired"
                        403 -> "Tidak memiliki izin untuk mengakses data user"
                        404 -> "Endpoint current user tidak ditemukan"
                        500 -> "Server error, coba lagi nanti"
                        else -> "Gagal memuat current user: ${response.message()}"
                    }
                    Log.e("AuthViewModel", "❌ HTTP Error ${response.code()}: $errorMessage")
                    _currentUser.value = UiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                val errorMsg = "Network error: ${e.message}"
                Log.e("AuthViewModel", "❌ Exception: $errorMsg", e)
                _currentUser.value = UiState.Error(errorMsg)
            }
        }
    }

    fun fetchUserByName(nama: String) {
        viewModelScope.launch {
            _currentUser.value = UiState.Loading
            try {
                val response = repository.getAllUsers()
                if (response.isSuccessful && response.body()?.success == true) {
                    val userItemList = response.body()?.data ?: emptyList()
                    val foundUser = userItemList.find { it.nama == nama }
                    if (foundUser != null) {
                        val displayUser = foundUser.toDisplayUser()
                        _currentUser.value = UiState.Success(displayUser)
                    } else {
                        _currentUser.value = UiState.Error("User dengan nama '$nama' tidak ditemukan")
                    }
                } else {
                    val errorMessage = response.body()?.message ?: "Gagal memuat data user"
                    _currentUser.value = UiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _currentUser.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun addUser(nama: String, password: String, role: String) {
        viewModelScope.launch {
            _addUserResult.value = UiState.Loading
            try {

                val validRole = if (role.isBlank()) "kasir" else role


                val user = User(nama = nama, password = password, role = validRole)

                val response = repository.addUser(user)

                if (response.isSuccessful) {
                    Log.d("AuthViewModel", "✅ User berhasil ditambahkan")
                    _addUserResult.value = UiState.Success(Unit)
                    fetchAllUsers()
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Data user tidak valid"
                        409 -> "Nama user sudah terdaftar"
                        500 -> "Server error, coba lagi nanti"
                        else -> "Gagal menambah user: ${response.message()}"
                    }
                    Log.e("AuthViewModel", "❌ Gagal menambah user: $errorMessage")
                    _addUserResult.value = UiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _addUserResult.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun updateUser(id: String, user: User) {
        viewModelScope.launch {
            _updateUserResult.value = UiState.Loading
            try {
                val response = repository.updateUser(id, user)
                if (response.isSuccessful) {
                    _updateUserResult.value = UiState.Success(Unit)
                    fetchAllUsers()
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Data user tidak valid"
                        404 -> "User tidak ditemukan"
                        409 -> "Nama user sudah digunakan"
                        500 -> "Server error, coba lagi nanti"
                        else -> "Gagal mengupdate user: ${response.message()}"
                    }
                    _updateUserResult.value = UiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _updateUserResult.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun updateUserPassword(id: String, newPassword: String, currentDisplayUser: DisplayUser) {
        val updatedUser = User(nama = currentDisplayUser.nama, password = newPassword, role = currentDisplayUser.role)
        updateUser(id, updatedUser)
    }

    fun updateUserRole(id: String, newRole: String, currentDisplayUser: DisplayUser) {
        val updatedUser = User(nama = currentDisplayUser.nama, password = "", role = newRole)
        updateUser(id, updatedUser)
    }

    fun deleteUser(id: String) {
        viewModelScope.launch {
            _deleteUserResult.value = UiState.Loading
            try {
                val response = repository.deleteUser(id)
                if (response.isSuccessful) {
                    _deleteUserResult.value = UiState.Success(Unit)
                    fetchAllUsers()
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "User tidak ditemukan"
                        403 -> "Tidak memiliki izin untuk menghapus user"
                        409 -> "User tidak dapat dihapus karena masih memiliki data terkait"
                        500 -> "Server error, coba lagi nanti"
                        else -> "Gagal menghapus user: ${response.message()}"
                    }
                    _deleteUserResult.value = UiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _deleteUserResult.value = UiState.Error("Error: ${e.message}")
            }
        }
    }
}
