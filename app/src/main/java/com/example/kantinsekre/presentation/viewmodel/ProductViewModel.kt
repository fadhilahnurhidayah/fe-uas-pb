package com.example.kantinsekre.presentation.viewmodel

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kantinsekre.R

class ProductViewModel : Fragment() {

    companion object {
        fun newInstance() = ProductViewModel()
    }

    private val viewModel: ProductViewModelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_product_view_model, container, false)
    }
}