//package com.example.kantinsekre.presentation.report
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import com.example.kantinsekre.R
//import com.example.kantinsekre.util.DummyDataProvider
//import java.text.SimpleDateFormat
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//import java.util.*
//
//class ReportFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_report, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val todaySalesTV = view.findViewById<TextView>(R.id.today_sales_value)
//        val weekSalesTV = view.findViewById<TextView>(R.id.week_sales_value)
//        val monthSalesTV = view.findViewById<TextView>(R.id.month_sales_value)
//        val totalItemsTV = view.findViewById<TextView>(R.id.total_items_value)
//        val dateTV = view.findViewById<TextView>(R.id.current_date)
//
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        val today = LocalDate.now()
//        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
//        val startOfMonth = today.withDayOfMonth(1)
//
//        var todaySales = 0
//        var weekSales = 0
//        var monthSales = 0
//        var totalItems = 0
//
//        for (transaction in DummyDataProvider.transactionList) {
//            val trxDate = LocalDate.parse(transaction.tanggal, formatter)
//
//            if (trxDate == today) {
//                todaySales += transaction.total
//            }
//            if (!trxDate.isBefore(startOfWeek)) {
//                weekSales += transaction.total
//            }
//            if (!trxDate.isBefore(startOfMonth)) {
//                monthSales += transaction.total
//            }
//        }
//
//        for (detail in DummyDataProvider.detailTransactionList) {
//            totalItems += detail.qty
//        }
//
//        todaySalesTV.text = "Rp ${formatCurrency(todaySales)}"
//        weekSalesTV.text = "Rp ${formatCurrency(weekSales)}"
//        monthSalesTV.text = "Rp ${formatCurrency(monthSales)}"
//        totalItemsTV.text = totalItems.toString()
//        dateTV.text = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
//    }
//
//    private fun formatCurrency(value: Int): String {
//        return String.format("%,d", value).replace(',', '.')
//    }
//}
