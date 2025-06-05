package com.example.kantinsekre.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.*

class TransactionAdapter(
    private val items: MutableList<Product>,
    private val onRemoveClick: (Product, Int) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemPriceUnit: TextView = itemView.findViewById(R.id.item_price_unit)
        val itemQuantity: TextView = itemView.findViewById(R.id.item_quantity)
        val itemTotal: TextView = itemView.findViewById(R.id.item_total)
        val removeButton: MaterialButton = itemView.findViewById(R.id.remove_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]

        holder.itemName.text = item.name
        holder.itemPriceUnit.text = "@${formatCurrency(item.price.toDouble())}"
        holder.itemQuantity.text = item.quantity.toString()

        val totalPrice = item.price.toDouble() * item.quantity.toDouble()
        holder.itemTotal.text = formatCurrency(totalPrice)

        holder.removeButton.setOnClickListener {
            onRemoveClick(item, position)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(amount)
    }
}