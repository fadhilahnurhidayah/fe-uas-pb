package com.example.kantinsekre.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.models.Transaksi
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.*

class TransactionAdapter(
    private val transactions: List<Transaksi>,
    private val onCancelClick: (Transaksi) -> Unit,
    private val onCompleteClick: (Transaksi) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        holder.transactionDate.text = transaction.tanggal
        holder.customerName.text = "Pembeli: ${transaction.namaPembeli}"
        holder.totalAmount.text = formatCurrency(transaction.totalHarga?.toDoubleOrNull() ?: 0.0)

        holder.btnCancel.setOnClickListener {
            onCancelClick(transaction)
        }

        holder.btnComplete.setOnClickListener {
            onCompleteClick(transaction)
        }

        // Disable buttons if transaction is already completed or cancelled
        val isCompleted = transaction.status == "selesai"
        val isCancelled = transaction.status == "dibatalkan"
        
        holder.btnCancel.isEnabled = !isCompleted && !isCancelled
        holder.btnComplete.isEnabled = !isCompleted && !isCancelled
    }

    override fun getItemCount(): Int = transactions.size

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(amount)
    }
}