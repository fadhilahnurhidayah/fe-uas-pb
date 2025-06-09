package com.example.kantinsekre.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kantinsekre.models.DailyReportResponse
import com.example.kantinsekre.models.MonthlyReportResponse
import com.example.kantinsekre.presentation.state.UiState
import com.example.kantinsekre.repository.ReportRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk menangani logika bisnis laporan
 * Memisahkan logika dari Fragment dan berkomunikasi dengan Repository
 */
class ReportViewModel(private val repository: ReportRepository) : ViewModel() {
    
    // LiveData untuk laporan harian
    private val _dailyReport = MutableLiveData<UiState<DailyReportResponse>>()
    val dailyReport: LiveData<UiState<DailyReportResponse>> = _dailyReport

    // LiveData untuk laporan bulanan
    private val _monthlyReport = MutableLiveData<UiState<MonthlyReportResponse>>()
    val monthlyReport: LiveData<UiState<MonthlyReportResponse>> = _monthlyReport

    /**
     * Mengambil laporan harian dari repository
     */
    fun fetchDailyReport() {
        viewModelScope.launch {
            _dailyReport.value = UiState.Loading
            try {
                val response = repository.getLaporanHarian()
                if (response.isSuccessful) {
                    response.body()?.let { reportData ->
                        _dailyReport.value = UiState.Success(reportData)
                    } ?: run {
                        _dailyReport.value = UiState.Error("Data laporan harian kosong")
                    }
                } else {
                    _dailyReport.value = UiState.Error("Gagal memuat laporan harian")
                }
            } catch (e: Exception) {
                _dailyReport.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Mengambil laporan harian berdasarkan tanggal
     * @param tanggal Tanggal laporan yang diinginkan (format: yyyy-MM-dd)
     */
    fun fetchDailyReportByDate(tanggal: String) {
        viewModelScope.launch {
            _dailyReport.value = UiState.Loading
            try {
                val response = repository.getLaporanHarianByDate(tanggal)
                if (response.isSuccessful) {
                    response.body()?.let { reportData ->
                        _dailyReport.value = UiState.Success(reportData)
                    } ?: run {
                        _dailyReport.value = UiState.Error("Data laporan harian kosong")
                    }
                } else {
                    _dailyReport.value = UiState.Error("Gagal memuat laporan harian")
                }
            } catch (e: Exception) {
                _dailyReport.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Mengambil laporan bulanan dari repository
     */
    fun fetchMonthlyReport() {
        viewModelScope.launch {
            _monthlyReport.value = UiState.Loading
            try {
                val response = repository.getLaporanBulanan()
                if (response.isSuccessful) {
                    response.body()?.let { reportData ->
                        _monthlyReport.value = UiState.Success(reportData)
                    } ?: run {
                        _monthlyReport.value = UiState.Error("Data laporan bulanan kosong")
                    }
                } else {
                    _monthlyReport.value = UiState.Error("Gagal memuat laporan bulanan")
                }
            } catch (e: Exception) {
                _monthlyReport.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Mengambil laporan bulanan berdasarkan bulan
     * @param bulan Bulan laporan yang diinginkan (format: yyyy-MM)
     */
    fun fetchMonthlyReportByMonth(bulan: String) {
        viewModelScope.launch {
            _monthlyReport.value = UiState.Loading
            try {
                val response = repository.getLaporanBulananByMonth(bulan)
                if (response.isSuccessful) {
                    response.body()?.let { reportData ->
                        _monthlyReport.value = UiState.Success(reportData)
                    } ?: run {
                        _monthlyReport.value = UiState.Error("Data laporan bulanan kosong")
                    }
                } else {
                    _monthlyReport.value = UiState.Error("Gagal memuat laporan bulanan")
                }
            } catch (e: Exception) {
                _monthlyReport.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Reset state daily report
     */
    fun resetDailyReport() {
        _dailyReport.value = UiState.Idle
    }

    /**
     * Reset state monthly report
     */
    fun resetMonthlyReport() {
        _monthlyReport.value = UiState.Idle
    }
}
