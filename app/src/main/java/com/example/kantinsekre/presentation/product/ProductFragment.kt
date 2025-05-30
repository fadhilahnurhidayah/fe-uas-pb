package com.example.kantinsekre.presentation.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kantinsekre.adapters.ProductAdapter
import com.example.kantinsekre.databinding.FragmentProductBinding
import com.example.kantinsekre.models.Menu
import com.example.kantinsekre.network.ApiClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private val allProducts = mutableListOf<Menu>()

    private val filteredProducts = mutableListOf<Menu>()
    private lateinit var productAdapter: ProductAdapter

    private fun fetchMenuFromApi() {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.create(requireContext() )
                val response = apiService.getAllMenu()
                allProducts.clear()
                allProducts.addAll(response.data)
                if (isAdded && _binding != null) {
                    filterProducts(binding.searchProducts.query?.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Gagal memuat menu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
        fetchMenuFromApi()

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
                it.nama.lowercase().contains(searchQuery) ||
                        it.jenis.lowercase().contains(searchQuery)
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
        val categories = allProducts.map { it.harga }.distinct().toTypedArray()
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
                it.jenis in selectedCategories
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
//            allProducts.add(newProduct)
            filterProducts(binding.searchProducts.query?.toString())
        }
        dialog.show(childFragmentManager, "AddProductDialog")
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}