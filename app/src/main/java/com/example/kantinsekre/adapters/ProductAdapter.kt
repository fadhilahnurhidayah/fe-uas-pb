package com.example.kantinsekre.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.models.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.LinearLayout

class ProductAdapter(
    private val products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Using the new layout IDs from the MaterialCardView layout
        val productName: TextView = itemView.findViewById(R.id.tvProductName)
        val productPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val productCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val productImage: ImageView = itemView.findViewById(R.id.imageProduct)

        // Additional views from the new layout
        val viewOverlay: View = itemView.findViewById(R.id.viewOverlay)
        val layoutActions: LinearLayout = itemView.findViewById(R.id.layoutActions)
        val fabEdit: FloatingActionButton = itemView.findViewById(R.id.fabEdit)
        val fabDelete: FloatingActionButton = itemView.findViewById(R.id.fabDelete)
        val tvAvailability: TextView = itemView.findViewById(R.id.tvAvailability)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        // Set product information
        holder.productName.text = product.name
        holder.productCategory.text = product.category

        // Format price with proper currency symbol and formatting
        holder.productPrice.text = String.format("Rp %,d", product.price)

        // Set default image resource
        holder.productImage.setImageResource(R.drawable.placeholder_food)

        // Set up action buttons
        setupActionButtons(holder, product)

        // Set click listener on the entire view
        holder.itemView.setOnClickListener {
            onProductClick(product)
        }

        // Long press to show edit/delete options
        holder.itemView.setOnLongClickListener {
            toggleActionOverlay(holder, true)
            true
        }
    }

    private fun setupActionButtons(holder: ProductViewHolder, product: Product) {
        // Edit button click listener
        holder.fabEdit.setOnClickListener {
            // Handle edit action
            toggleActionOverlay(holder, false)
            // You could trigger an edit dialog or navigate to edit screen
            // For now, just use the same product click handler
            onProductClick(product)
        }

        // Delete button click listener
        holder.fabDelete.setOnClickListener {
            // Handle delete action
            toggleActionOverlay(holder, false)
            // Implement delete functionality here
        }

        // Clicking anywhere on the overlay dismisses it
        holder.viewOverlay.setOnClickListener {
            toggleActionOverlay(holder, false)
        }
    }

    private fun toggleActionOverlay(holder: ProductViewHolder, show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        holder.viewOverlay.visibility = visibility
        holder.layoutActions.visibility = visibility
    }

    override fun getItemCount() = products.size
}