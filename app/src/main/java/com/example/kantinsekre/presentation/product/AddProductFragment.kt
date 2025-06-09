package com.example.kantinsekre.presentation.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.kantinsekre.databinding.DialogAddProductBinding
import com.example.kantinsekre.models.CreateMenu
import com.example.kantinsekre.presentation.state.UiState
import com.example.kantinsekre.presentation.viewmodel.ProductViewModel
import com.example.kantinsekre.presentation.viewmodel.ViewModelFactory

class AddProductDialogFragment : DialogFragment() {

    private var _binding: DialogAddProductBinding? = null
    private val binding get() = _binding!!

    var onProductAdded: ((CreateMenu) -> Unit)? = null

    // ViewModel dengan ViewModelFactory
    private val productViewModel: ProductViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        val categories = arrayOf("Makanan", "Minuman")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.autoCompleteCategory.setAdapter(adapter)

        binding.btnCancelNewCategory.setOnClickListener {
            binding.editTextNewCategory.setText("") // Clear input
            binding.layoutNewCategory.visibility = View.GONE
            binding.layoutCategoryDropdown.visibility = View.VISIBLE
        }

        binding.btnAddProduct.setOnClickListener {
            addNewProduct()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    /**
     * Observe ViewModel untuk hasil add product
     */
    private fun observeViewModel() {
        productViewModel.addProductResult.observe(this) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    // Disable button saat loading
                    binding.btnAddProduct.isEnabled = false
                    binding.btnAddProduct.text = "Adding..."
                }
                is UiState.Success -> {
                    binding.btnAddProduct.isEnabled = true
                    binding.btnAddProduct.text = "Add Product"
                    Toast.makeText(requireContext(), "Product added successfully!", Toast.LENGTH_SHORT).show()
                    onProductAdded?.invoke(CreateMenu("", "", "")) // Dummy data, actual data handled by ViewModel
                    dismiss()
                }
                is UiState.Error -> {
                    binding.btnAddProduct.isEnabled = true
                    binding.btnAddProduct.text = "Add Product"
                    Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.Idle -> {
                    binding.btnAddProduct.isEnabled = true
                    binding.btnAddProduct.text = "Add Product"
                }
            }
        }
    }

    private fun addNewProduct() {
        val nama = binding.editTextName.text.toString().trim()
        val harga = binding.editTextPrice.text.toString().trim()

        val jenis = if (binding.layoutNewCategory.visibility == View.VISIBLE) {
            binding.editTextNewCategory.text.toString().trim()
        } else {
            binding.autoCompleteCategory.text.toString().trim()
        }

        if (nama.isEmpty() || harga.isEmpty() || jenis.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val newProduct = CreateMenu(
            nama = nama,
            jenis = jenis,
            harga = harga,
        )

        // Gunakan ViewModel untuk add product
        productViewModel.addMenu(newProduct)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}