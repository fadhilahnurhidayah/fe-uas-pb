package com.example.kantinsekre.presentation.state

/**
 * Sealed class untuk menangani berbagai state UI
 * Membantu dalam mengelola loading, success, dan error states
 */
sealed class UiState<out T> {
    /**
     * State ketika sedang loading/memuat data
     */
    object Loading : UiState<Nothing>()
    
    /**
     * State ketika berhasil mendapatkan data
     * @param data Data yang berhasil dimuat
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * State ketika terjadi error
     * @param message Pesan error yang akan ditampilkan
     */
    data class Error(val message: String) : UiState<Nothing>()
    
    /**
     * State awal/idle
     */
    object Idle : UiState<Nothing>()
}

/**
 * Extension function untuk mengecek apakah state sedang loading
 */
fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading

/**
 * Extension function untuk mengecek apakah state berhasil
 */
fun <T> UiState<T>.isSuccess(): Boolean = this is UiState.Success

/**
 * Extension function untuk mengecek apakah state error
 */
fun <T> UiState<T>.isError(): Boolean = this is UiState.Error

/**
 * Extension function untuk mendapatkan data jika state success
 */
fun <T> UiState<T>.getDataOrNull(): T? = if (this is UiState.Success) data else null

/**
 * Extension function untuk mendapatkan error message jika state error
 */
fun <T> UiState<T>.getErrorMessage(): String? = if (this is UiState.Error) message else null
