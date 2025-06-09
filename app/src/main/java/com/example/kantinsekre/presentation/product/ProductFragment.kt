package com.example.kantinsekre.presentation.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kantinsekre.adapters.ProductAdapter
import com.example.kantinsekre.databinding.FragmentProductBinding
import com.example.kantinsekre.models.Menu
import com.example.kantinsekre.presentation.viewmodel.SharedViewModel
import com.example.kantinsekre.presentation.state.UiState
import com.example.kantinsekre.presentation.viewmodel.ProductViewModel
import com.example.kantinsekre.presentation.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private val allProducts = mutableListOf<Menu>()
    private val filteredProducts = mutableListOf<Menu>()
    private lateinit var productAdapter: ProductAdapter

    // ViewModel dengan ViewModelFactory
    private val productViewModel: ProductViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    // SharedViewModel untuk user data
    private val sharedViewModel: SharedViewModel by activityViewModels()

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
        setupWelcomeMessage()
        setupObservers()

        // Fetch data menggunakan ViewModel
        productViewModel.fetchAllMenu()
    }

    /**
     * Setup observers untuk ViewModel
     */
    private fun setupObservers() {
        // Observe products data
        productViewModel.products.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    // Tampilkan loading indicator jika diperlukan
                    // binding.progressBar.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    // binding.progressBar.visibility = View.GONE
                    allProducts.clear()
                    allProducts.addAll(uiState.data)
                    filterProducts(binding.searchProducts.query?.toString())
                }
                is UiState.Error -> {
                    // binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.Idle -> {
                    // State awal, tidak perlu action
                }
            }
        }

        // Observe delete product result
        productViewModel.deleteProductResult.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    // Show loading if needed
                }
                is UiState.Success -> {
                    Toast.makeText(requireContext(), "Menu berhasil dihapus", Toast.LENGTH_SHORT).show()
                    productViewModel.resetDeleteProductResult()
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), "Gagal menghapus menu: ${uiState.message}", Toast.LENGTH_SHORT).show()
                    productViewModel.resetDeleteProductResult()
                }
                is UiState.Idle -> {
                    // State awal, tidak perlu action
                }
            }
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            products = filteredProducts,
            onProductClick = { product ->
                // Handle product click if needed
            },
            onDeleteClick = { product ->
                showDeleteConfirmationDialog(product)
            }
        )

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
        val categories = allProducts.map { it.jenis }.distinct().toTypedArray()
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

    /**
     * Setup welcome message dengan data user
     */
    private fun setupWelcomeMessage() {
        // Observe current user dari SharedViewModel
        sharedViewModel.currentUser.observe(viewLifecycleOwner) { currentUser ->
            if (currentUser != null) {
                val welcomeText = "Selamat datang, ${currentUser.nama}!"
                binding.welcomeMessage.text = welcomeText
            } else {
                binding.welcomeMessage.text = "Selamat datang!"
            }
        }
    }

    private fun showAddProductDialog() {
        val dialog = AddProductDialogFragment()
        dialog.onProductAdded = {
            // Refresh data menggunakan ViewModel
            productViewModel.fetchAllMenu()
        }
        dialog.show(childFragmentManager, "AddProductDialog")
    }

    /**
     * Menampilkan dialog konfirmasi untuk menghapus menu
     */
    private fun showDeleteConfirmationDialog(menu: Menu) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Menu")
            .setMessage("Apakah Anda yakin ingin menghapus menu \"${menu.nama}\"?\n\nTindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Ya, Hapus") { _, _ ->
                productViewModel.deleteMenu(menu.id.toString())
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}