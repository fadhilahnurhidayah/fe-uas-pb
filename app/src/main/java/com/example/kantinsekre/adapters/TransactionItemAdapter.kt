package com.example.kantinsekre.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.models.ItemRequest

class TransactionItemAdapter(
    private val items: MutableList<ItemRequest>,
    private val onDeleteClick: (ItemRequest) -> Unit
) : RecyclerView.Adapter<TransactionItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemQuantity: TextView = view.findViewById(R.id.item_quantity)
        val deleteButton: View = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.namaMenu
        holder.itemQuantity.text = "Jumlah: ${item.jumlahMenu}"
        holder.deleteButton.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount() = items.size

    fun addItem(item: ItemRequest) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun removeItem(item: ItemRequest) {
        val position = items.indexOf(item)
        if (position != -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }
} 