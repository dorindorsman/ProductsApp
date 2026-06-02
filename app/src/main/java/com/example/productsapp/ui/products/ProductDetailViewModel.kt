package com.example.productsapp.ui.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsapp.domain.model.Product
import com.example.productsapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle["productId"])

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val product = repository.getProductById(productId)
            if (product != null) {
                _uiState.update { it.copy(product = product, isLoading = false) }
            } else {
                _uiState.update { it.copy(error = "Product not found", isLoading = false) }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.value.product?.let {
                repository.toggleFavorite(it.id)
                loadProduct()
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.addOrUpdateProduct(product)
            loadProduct()
        }
    }

    fun deleteProduct() {
        viewModelScope.launch {
            _uiState.value.product?.let {
                repository.deleteProduct(it.id)
            }
        }
    }
}