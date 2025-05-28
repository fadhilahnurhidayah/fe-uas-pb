package com.example.kantinsekre.presentation.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kantinsekre.adapters.ProductAdapter
import com.example.kantinsekre.databinding.FragmentProductBinding
import com.example.kantinsekre.models.Product
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private val allProducts = mutableListOf(
        Product(1, "Nasi Goreng", 15000, "Makanan", 10000),
        Product(2, "Mie Goreng", 12000, "Makanan", 8000),
        Product(3, "Es Teh", 5000, "Minuman", 2000))

    private val filteredProducts = mutableListOf<Product>()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupFilterButton()
        setupAddProductButton()

        // Initialize filteredProducts after allProducts are set
        filteredProducts.clear()
        filteredProducts.addAll(allProducts)
        updateProductCount()
        productAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(filteredProducts) { product ->
        }

        binding.productRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchProducts.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText)
                return true
            }
        })
    }

    private fun filterProducts(query: String?) {
        filteredProducts.clear()

        if (query.isNullOrEmpty()) {
            filteredProducts.addAll(allProducts)
        } else {
            val searchQuery = query.lowercase()
            allProducts.filterTo(filteredProducts) {
                it.name.lowercase().contains(searchQuery) ||
                        it.category.lowercase().contains(searchQuery)
            }
        }

        updateProductCount()
        productAdapter.notifyDataSetChanged()
    }

    private fun updateProductCount() {
        binding.productCount.text = "Showing ${filteredProducts.size} items"
    }

    private fun setupFilterButton() {
        binding.filtersButton.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        val categories = allProducts.map { it.category }.distinct().toTypedArray()
        val checkedItems = BooleanArray(categories.size) { true }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Filter by Category")
            .setMultiChoiceItems(categories, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Apply") { _, _ ->
                applyFilters(categories, checkedItems)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun applyFilters(categories: Array<String>, checkedItems: BooleanArray) {
        val selectedCategories = categories.filterIndexed { index, _ -> checkedItems[index] }

        filteredProducts.clear()
        if (selectedCategories.isEmpty()) {
            filteredProducts.addAll(allProducts)
        } else {
            allProducts.filterTo(filteredProducts) {
                it.category in selectedCategories
            }
        }

        updateProductCount()
        productAdapter.notifyDataSetChanged()
    }

    private fun setupAddProductButton() {
        binding.addProductButton.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun showAddProductDialog() {
        val dialog = AddProductDialogFragment()
        dialog.onProductAdded = { newProduct ->
            allProducts.add(newProduct)
            filterProducts(binding.searchProducts.query?.toString())
        }
        dialog.show(childFragmentManager, "AddProductDialog")
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}