package com.example.kantinsekre.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.models.Product
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.*

class TransactionAdapter(
    private val items: List<Product>,
    private val onRemoveClick: (Product, Int) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.item_card)
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)
        val itemQuantity: TextView = itemView.findViewById(R.id.item_quantity)
        val itemTotal: TextView = itemView.findViewById(R.id.item_total)
        val removeButton: MaterialButton = itemView.findViewById(R.id.remove_button)
        val quantityLabel: TextView = itemView.findViewById(R.id.quantity_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        // Set item name with better styling
        holder.itemName.text = item.name

        // Format and set price
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        holder.itemPrice.text = currencyFormat.format(item.price.toLong())

        // Set quantity with better formatting
        holder.itemQuantity.text = item.quantity.toString()
        holder.quantityLabel.text = if (item.quantity == 1) "qty" else "qty"

        // Calculate and set total
        val total = item.price * item.quantity
        holder.itemTotal.text = currencyFormat.format(total.toLong())

        // Style the card based on position (alternating colors or effects)
        holder.cardView.apply {
            strokeWidth = 2
            strokeColor = context.getColor(R.color.primary_light)
            elevation = 4f
            radius = 12f
        }

        // Set remove button click listener
        holder.removeButton.setOnClickListener {
            onRemoveClick(item, position)
        }

        // Add subtle animation effect
        holder.cardView.setOnClickListener {
            // Optional: Add click feedback or item details
        }
    }

    override fun getItemCount() = items.size
}