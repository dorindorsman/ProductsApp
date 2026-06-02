package com.example.productsapp.ui.products

import com.example.productsapp.domain.model.Product

data class ProductsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val sortBy: String = "default",
    val showAddEditDialog: Boolean = false,
    val editingProduct: Product? = null
)
