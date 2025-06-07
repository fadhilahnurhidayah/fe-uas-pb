package com.example.kantinsekre.presentation.product

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.kantinsekre.databinding.DialogAddProductBinding
import com.example.kantinsekre.models.CreateMenu
import com.example.kantinsekre.network.ApiClient
import kotlinx.coroutines.launch

class AddProductDialogFragment : DialogFragment() {

    private var _binding: DialogAddProductBinding? = null
    private val binding get() = _binding!!

    var onProductAdded: ((CreateMenu) -> Unit)? = null

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

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        val categories = arrayOf("Makanan", "Minuman")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, categories)
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

        lifecycleScope.launch {
            try {
                val apiService = ApiClient.create(requireContext())
                val response = apiService.addMenu(newProduct)
                println(newProduct)

                if (response.isSuccessful) {
                    onProductAdded?.invoke(newProduct)
                    Toast.makeText(requireContext(), "${newProduct.nama} added!", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Failed to add product", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}