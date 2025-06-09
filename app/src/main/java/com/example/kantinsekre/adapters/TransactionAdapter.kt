package com.example.kantinsekre.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.models.Transaksi
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TransactionAdapter(
    private val transactions: List<Transaksi>,
    private val onCancelClick: (Transaksi) -> Unit,
    private val onCompleteClick: (Transaksi) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionCard: CardView = itemView.findViewById(R.id.transaction_card)
        val transactionId: TextView = itemView.findViewById(R.id.transaction_id)
        val transactionDate: TextView = itemView.findViewById(R.id.transaction_date)
        val customerName: TextView = itemView.findViewById(R.id.customer_name)
        val totalAmount: TextView = itemView.findViewById(R.id.total_amount)
        val btnCancel: MaterialButton = itemView.findViewById(R.id.btn_cancel)
        val btnComplete: MaterialButton = itemView.findViewById(R.id.btn_complete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.transactionId.text = "ID: ${transaction.id}"
        holder.transactionDate.text = formatTransactionDate(transaction.tanggal)
        holder.customerName.text = "Pembeli: ${transaction.namaPembeli}"
        holder.totalAmount.text = formatCurrency(transaction.totalHarga?.toDoubleOrNull() ?: 0.0)

        setCardBorderColor(holder.transactionCard, transaction.status)

        holder.btnCancel.setOnClickListener {
            onCancelClick(transaction)
        }

        holder.btnComplete.setOnClickListener {
            onCompleteClick(transaction)
        }

        val isCompleted = transaction.status == "selesai"
        val isCancelled = transaction.status == "dibatalkan"

        holder.btnCancel.isEnabled = !isCompleted && !isCancelled
        holder.btnComplete.isEnabled = !isCompleted && !isCancelled
    }

    override fun getItemCount(): Int = transactions.size

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(amount)
    }

    /**
     * Set warna border card berdasarkan status transaksi
     * @param cardView CardView yang akan diubah border-nya
     * @param status Status transaksi (pending, selesai, dibatalkan)
     */
    private fun setCardBorderColor(cardView: CardView, status: String) {
        val context = cardView.context
        val backgroundDrawable = when (status.lowercase()) {
            "pending" -> ContextCompat.getDrawable(context, R.drawable.card_border_pending)
            "selesai" -> ContextCompat.getDrawable(context, R.drawable.card_border_completed)
            "dibatalkan" -> ContextCompat.getDrawable(context, R.drawable.card_border_cancelled)
            else -> ContextCompat.getDrawable(context, R.drawable.card_border_default)
        }
        cardView.background = backgroundDrawable
    }

    private fun formatTransactionDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return dateString

            val now = Calendar.getInstance()
            val transactionDate = Calendar.getInstance().apply { time = date }

            val diffInMillis = now.timeInMillis - transactionDate.timeInMillis
            val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

            when {
                diffInDays == 0L -> {
                    // Hari ini - tampilkan dengan jam
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    "Hari ini, ${timeFormat.format(date)}"
                }
                diffInDays == 1L -> "Kemarin"
                diffInDays in 2..6 -> "${diffInDays} hari yang lalu"
                diffInDays in 7..13 -> "1 minggu yang lalu"
                diffInDays in 14..29 -> "${diffInDays / 7} minggu yang lalu"
                else -> {
                    // Lebih dari sebulan - tampilkan tanggal lengkap
                    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                    outputFormat.format(date)
                }
            }
        } catch (e: Exception) {
            // Jika parsing gagal, kembalikan string asli
            dateString
        }
    }
}