package com.example.kantinsekre.presentation.product

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.kantinsekre.databinding.DialogAddProductBinding
import com.example.kantinsekre.models.createmenu

class AddProductDialogFragment : DialogFragment() {

    private var _binding: DialogAddProductBinding? = null
    private val binding get() = _binding!!

    var onProductAdded: ((createmenu) -> Unit)? = null

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

        binding.btnNewCategory.setOnClickListener {
            binding.layoutCategoryDropdown.visibility = View.GONE
            binding.layoutNewCategory.visibility = View.VISIBLE
        }

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
        val jenis = binding.editTextPrice.text.toString().trim()
        val harga = binding.editTextCostPrice.text.toString().trim()

        val category = if (binding.layoutNewCategory.visibility == View.VISIBLE) {
            binding.editTextNewCategory.text.toString().trim()
        } else {
            binding.autoCompleteCategory.text.toString().trim()
        }

        if (nama.isEmpty() || jenis.isEmpty() || harga.isEmpty() || category.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        try {

            val newProductId = (System.currentTimeMillis() % 100000).toInt() + 1000 // Simple unique ID

            val newProduct = createmenu(
                nama = nama,
                jenis = jenis,
                harga = harga,
            )

            onProductAdded?.invoke(newProduct)

            Toast.makeText(requireContext(), "${newProduct.nama} added!", Toast.LENGTH_SHORT).show()
            dismiss()
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Price and Cost Price must be valid numbers", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}