package com.example.kantinsekre.presentation.report

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kantinsekre.R
import com.example.kantinsekre.network.ApiService
import com.example.kantinsekre.network.ApiClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment : Fragment() {

    private lateinit var apiService: ApiService
    private lateinit var todaySalesTV: TextView
    private lateinit var weekSalesTV: TextView
    private lateinit var monthSalesTV: TextView
    private lateinit var totalItemsTV: TextView
    private lateinit var dateTV: TextView

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
        loadReportData()
    }

    private fun initializeViews(view: View) {
        apiService = ApiClient.create(requireContext())
        todaySalesTV = view.findViewById(R.id.today_sales_value)
        weekSalesTV = view.findViewById(R.id.week_sales_value)
        monthSalesTV = view.findViewById(R.id.month_sales_value)
        totalItemsTV = view.findViewById(R.id.total_items_value)
        dateTV = view.findViewById(R.id.current_date)

        // Set default values
        todaySalesTV.text = "Rp 0"
        weekSalesTV.text = "Rp 0"
        monthSalesTV.text = "Rp 0"
        totalItemsTV.text = "0"
    }

    private fun setupDate() {
        val locale = Locale("id", "ID")
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", locale)
        val today = dateFormat.format(Date())
        dateTV.text = today
    }

    private fun loadReportData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d(TAG, "Memulai pengambilan data laporan")
                val response = apiService.getLaporanHarian()
                
                if (response.isSuccessful) {
                    Log.d(TAG, "Response berhasil: ${response.code()}")
                    val responseBody = response.body()
                    
                    if (responseBody?.success == true) {
                        val laporanHarian = responseBody.data
                        Log.d(TAG, "Jumlah data laporan: ${laporanHarian.size}")
                        
                        if (laporanHarian.isEmpty()) {
                            Log.d(TAG, "Data laporan kosong")
                            showError("Belum ada data laporan")
                            return@launch
                        }
                        
                        // Format tanggal untuk perbandingan
                        val locale = Locale("id", "ID")
                        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", locale)
                        val today = dateFormat.format(Date())
                        Log.d(TAG, "Tanggal hari ini: $today")
                        
                        // Hitung total untuk hari ini
                        val todayReport = laporanHarian.firstOrNull { it.tanggal == today }
                        val todaySales = todayReport?.total_pendapatan?.toInt() ?: 0
                        Log.d(TAG, "Total penjualan hari ini: $todaySales")
                        
                        // Hitung total untuk minggu ini
                        val calendar = Calendar.getInstance()
                        val weekStart = calendar.apply {
                            set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                        }.time
                        Log.d(TAG, "Awal minggu: ${dateFormat.format(weekStart)}")
                        
                        val weekReports = laporanHarian.filter { 
                            try {
                                val reportDate = dateFormat.parse(it.tanggal)
                                val isValid = reportDate != null && !reportDate.before(weekStart)
                                Log.d(TAG, "Tanggal: ${it.tanggal}, Valid: $isValid")
                                isValid
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing tanggal: ${it.tanggal}", e)
                                false
                            }
                        }
                        val weekSales = weekReports.sumOf { it.total_pendapatan?.toInt() ?: 0 }
                        Log.d(TAG, "Total penjualan minggu ini: $weekSales")
                        
                        // Hitung total untuk bulan ini
                        val monthStart = calendar.apply {
                            set(Calendar.DAY_OF_MONTH, 1)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                        }.time
                        Log.d(TAG, "Awal bulan: ${dateFormat.format(monthStart)}")
                        
                        val monthReports = laporanHarian.filter { 
                            try {
                                val reportDate = dateFormat.parse(it.tanggal)
                                val isValid = reportDate != null && !reportDate.before(monthStart)
                                Log.d(TAG, "Tanggal: ${it.tanggal}, Valid: $isValid")
                                isValid
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing tanggal: ${it.tanggal}", e)
                                false
                            }
                        }
                        val monthSales = monthReports.sumOf { it.total_pendapatan?.toInt() ?: 0 }
                        Log.d(TAG, "Total penjualan bulan ini: $monthSales")
                        
                        // Hitung total transaksi
                        val totalTransactions = monthReports.sumOf { it.total_transaksi ?: 0 }
                        Log.d(TAG, "Total transaksi: $totalTransactions")

                        // Update UI
                        updateUI(todaySales, weekSales, monthSales, totalTransactions)
                    } else {
                        val errorMessage = responseBody?.message ?: "Unknown error"
                        Log.e(TAG, "Response tidak sukses: $errorMessage")
                        showError("Gagal memuat data laporan: $errorMessage")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Response error: ${response.code()} - ${response.message()}")
                    Log.e(TAG, "Error body: $errorBody")
                    showError("Gagal memuat data laporan: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saat memuat data", e)
                showError("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    private fun updateUI(todaySales: Int, weekSales: Int, monthSales: Int, totalTransactions: Int) {
        try {
            todaySalesTV.text = "Rp ${formatCurrency(todaySales)}"
            weekSalesTV.text = "Rp ${formatCurrency(weekSales)}"
            monthSalesTV.text = "Rp ${formatCurrency(monthSales)}"
            totalItemsTV.text = formatNumber(totalTransactions)
            Log.d(TAG, "UI berhasil diupdate")
        } catch (e: Exception) {
            Log.e(TAG, "Error saat update UI", e)
        }
    }

    private fun showError(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun formatCurrency(value: Int): String {
        return String.format("%,d", value).replace(',', '.')
    }

    private fun formatNumber(value: Int): String {
        return String.format("%,d", value).replace(',', '.')
    }
}
