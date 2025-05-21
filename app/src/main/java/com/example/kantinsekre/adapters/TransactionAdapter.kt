package com.example.kantinsekre.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.models.Product

class TransactionAdapter(
    private val items: List<Product>,
    private val onRemoveClick: (Product, Int) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)
        val itemQuantity: TextView = itemView.findViewById(R.id.item_quantity)
        val itemTotal: TextView = itemView.findViewById(R.id.item_total)
        val removeButton: ImageButton = itemView.findViewById(R.id.remove_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]

        holder.itemName.text = item.name
        holder.itemPrice.text = String.format("$%.2f", item.price)
        holder.itemQuantity.text = item.quantity.toString()
        holder.itemTotal.text = String.format("$%.2f", item.price * item.quantity)

        holder.removeButton.setOnClickListener {
            onRemoveClick(item, position)
        }
    }

    override fun getItemCount() = items.size
}
