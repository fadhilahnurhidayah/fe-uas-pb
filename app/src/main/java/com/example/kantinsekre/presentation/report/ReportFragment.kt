package com.example.kantinsekre.presentation.report

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kantinsekre.R
import com.example.kantinsekre.models.DailyReportResponse
import com.example.kantinsekre.models.MonthlyReportResponse
import com.example.kantinsekre.presentation.state.UiState
import com.example.kantinsekre.presentation.viewmodel.ReportViewModel
import com.example.kantinsekre.presentation.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReportFragment : Fragment() {

    private lateinit var todaySalesTV: TextView
    private lateinit var weekSalesTV: TextView
    private lateinit var monthSalesTV: TextView
    private lateinit var totalItemsTV: TextView
    private lateinit var monthlyItemsTV: TextView
    private lateinit var monthlyTransactionsTV: TextView
    private lateinit var dateTV: TextView

    // ViewModel dengan ViewModelFactory
    private val reportViewModel: ReportViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    companion object {
        private const val TAG = "ReportFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupDate()
        observeViewModel()

        // Load data menggunakan ViewModel
        reportViewModel.fetchDailyReport()
        reportViewModel.fetchMonthlyReport()
    }

    /**
     * Observe ViewModel untuk perubahan data
     */
    private fun observeViewModel() {
        reportViewModel.dailyReport.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    // Show loading state if needed
                }
                is UiState.Success -> {
                    processDailyReportData(uiState.data)
                }
                is UiState.Error -> {
                    showError("Daily Report: ${uiState.message}")
                }
                is UiState.Idle -> {
                    // State awal, tidak perlu action
                }
            }
        }

        reportViewModel.monthlyReport.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    // Show loading state if needed
                }
                is UiState.Success -> {
                    processMonthlyReportData(uiState.data)
                }
                is UiState.Error -> {
                    showError("Monthly Report: ${uiState.message}")
                }
                is UiState.Idle -> {
                    // State awal, tidak perlu action
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initializeViews(view: View) {
        todaySalesTV = view.findViewById(R.id.today_sales_value)
        weekSalesTV = view.findViewById(R.id.week_sales_value)
        monthSalesTV = view.findViewById(R.id.month_sales_value)
        totalItemsTV = view.findViewById(R.id.total_items_value)
        monthlyItemsTV = view.findViewById(R.id.monthly_items_value)
        monthlyTransactionsTV = view.findViewById(R.id.monthly_transactions_value)
        dateTV = view.findViewById(R.id.current_date)

        // Set default values
        todaySalesTV.text = "Rp 0"
        weekSalesTV.text = "Rp 0"
        monthSalesTV.text = "Rp 0"
        totalItemsTV.text = "0"
        monthlyItemsTV.text = "0"
        monthlyTransactionsTV.text = "0"
    }

    private fun setupDate() {
        val locale = Locale("id", "ID")
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", locale)
        val today = dateFormat.format(Date())
        dateTV.text = today
    }

    /**
     * Process daily report data dari ViewModel
     */
    private fun processDailyReportData(responseBody: DailyReportResponse) {
        try {
            if (responseBody.success) {
                val laporanHarian = responseBody.data

                if (laporanHarian.isEmpty()) {
                    showError("Belum ada data laporan harian")
                    return
                }
                
                laporanHarian.forEach { report ->
                    Log.d(TAG, "Tanggal: '${report.tanggal}', Pendapatan: '${report.total_pendapatan}'")
                }

                // Format tanggal untuk perbandingan (yyyy-MM-dd)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = dateFormat.format(Date())
                Log.d(TAG, "Today formatted: '$today'")

                // Hitung total untuk hari ini - coba beberapa format tanggal
                var todayReport = laporanHarian.firstOrNull { it.tanggal == today }

                // Jika tidak ditemukan dengan format yyyy-MM-dd, coba format lain
                if (todayReport == null) {
                    val todayFormats = listOf(
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()),
                        SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()),
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                        SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
                    )

                    for (format in todayFormats) {
                        val todayFormatted = format.format(Date())
                        Log.d(TAG, "Trying format: '$todayFormatted'")
                        todayReport = laporanHarian.firstOrNull { it.tanggal == todayFormatted }
                        if (todayReport != null) {
                            Log.d(TAG, "Found match with format: '$todayFormatted'")
                            break
                        }
                    }
                }

                val todaySales = todayReport?.total_pendapatan?.toDoubleOrNull()?.toInt() ?: 0
                val todayTransactions = todayReport?.total_transaksi ?: 0

                Log.d(TAG, "Today sales: $todaySales, Today transactions: $todayTransactions")

                // Hitung total untuk minggu ini
                val calendar = Calendar.getInstance()
                val weekStart = calendar.apply {
                    set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }.time

                val weekReports = laporanHarian.filter {
                    try {
                        val reportDate = dateFormat.parse(it.tanggal)
                        reportDate != null && !reportDate.before(weekStart)
                    } catch (_: Exception) {
                        false
                    }
                }
                val weekSales = weekReports.sumOf { it.total_pendapatan.toDoubleOrNull()?.toInt() ?: 0 }

                // Update UI dengan data harian
                updateDailyUI(todaySales, weekSales, todayTransactions)

            } else {
                showError("Gagal memuat data laporan harian: ${responseBody.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saat memproses data harian", e)
            showError("Terjadi kesalahan: ${e.message}")
        }
    }

    /**
     * Process monthly report data dari ViewModel
     */
    private fun processMonthlyReportData(responseBody: MonthlyReportResponse) {
        try {
            if (responseBody.success == true) {
                val laporanBulanan = responseBody.data?.filterNotNull() ?: emptyList()

                if (laporanBulanan.isEmpty()) {
                    showError("Belum ada data laporan bulanan")
                    return
                }

                // Ambil data bulan ini
                val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
                val thisMonthReport = laporanBulanan.firstOrNull { report -> report.bulan == currentMonth }

                val monthSales = thisMonthReport?.totalPendapatan?.toDoubleOrNull()?.toInt() ?: 0
                val monthlyTransactions = thisMonthReport?.totalTransaksi ?: 0

                // Ambil data items dari bulan ini saja
                val monthlyItems = thisMonthReport?.totalTransaksi ?: 0

                // Update UI dengan data bulanan
                updateMonthlyUI(monthSales, monthlyItems, monthlyTransactions)

            } else {
                showError("Gagal memuat data laporan bulanan: ${responseBody.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saat memproses data bulanan", e)
            showError("Terjadi kesalahan: ${e.message}")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDailyUI(todaySales: Int, weekSales: Int, todayTransactions: Int) {
        try {
            todaySalesTV.text = "Rp ${formatCurrency(todaySales)}"
            weekSalesTV.text = "Rp ${formatCurrency(weekSales)}"
            totalItemsTV.text = formatNumber(todayTransactions)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating daily UI", e)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateMonthlyUI(monthSales: Int, monthlyItems: Int, monthlyTransactions: Int) {
        try {
            monthSalesTV.text = "Rp ${formatCurrency(monthSales)}"
            monthlyItemsTV.text = formatNumber(monthlyItems)
            monthlyTransactionsTV.text = formatNumber(monthlyTransactions)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating monthly UI", e)
        }
    }

    private fun showError(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatCurrency(value: Int): String {
        return String.format("%,d", value).replace(',', '.')
    }

    @SuppressLint("DefaultLocale")
    private fun formatNumber(value: Int): String {
        return String.format("%,d", value).replace(',', '.')
    }
}
