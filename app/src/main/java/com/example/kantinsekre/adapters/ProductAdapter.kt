package com.example.kantinsekre.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.models.Menu
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductAdapter(
    private val products: MutableList<Menu>,
    private val onProductClick: (Menu) -> Unit,
    private val onDeleteClick: (Menu) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tvProductName)
        val productPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val productCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val productImage: ImageView = itemView.findViewById(R.id.imageProduct)

        val viewOverlay: View = itemView.findViewById(R.id.viewOverlay)
        val layoutActions: LinearLayout = itemView.findViewById(R.id.layoutActions)
        val fabEdit: FloatingActionButton = itemView.findViewById(R.id.fabEdit)
        val fabDelete: FloatingActionButton = itemView.findViewById(R.id.fabDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.productName.text = product.nama
        holder.productCategory.text = product.jenis

        holder.productPrice.text = "Rp ${product.harga}"

        holder.productImage.setImageResource(R.drawable.placeholder_food)

        setupActionButtons(holder, product)

        holder.itemView.setOnClickListener {
            onProductClick(product)
        }

        holder.itemView.setOnLongClickListener {
            toggleActionOverlay(holder, true)
            true
        }
    }

    private fun setupActionButtons(holder: ProductViewHolder, product: Menu) {
        holder.fabEdit.setOnClickListener {
            toggleActionOverlay(holder, false)
            onProductClick(product)
        }

        holder.fabDelete.setOnClickListener {
            toggleActionOverlay(holder, false)
            onDeleteClick(product)
        }

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