package com.example.productsapp.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.productsapp.domain.model.Product
import com.example.productsapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val sortBy: String = "default",
    val showAddEditDialog: Boolean = false,
    val editingProduct: Product? = null
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortBy = MutableStateFlow("default")

    val products: Flow<PagingData<Product>> = combine(_searchQuery, _sortBy) { query, sort ->
        Pair(query, sort)
    }
        .debounce(300)
        .flatMapLatest { (query, sort) ->
            repository.getProducts(query, sort)
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSortChange(sortBy: String) {
        _sortBy.value = sortBy
        _uiState.update { it.copy(sortBy = sortBy) }
    }

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(productId)
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddEditDialog = true, editingProduct = null) }
    }

    fun showEditDialog(product: Product) {
        _uiState.update { it.copy(showAddEditDialog = true, editingProduct = product) }
    }

    fun hideDialog() {
        _uiState.update { it.copy(showAddEditDialog = false, editingProduct = null) }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            repository.addOrUpdateProduct(product)
            hideDialog()
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    fun resetLocalChanges() {
        viewModelScope.launch {
            repository.resetLocalChanges()
        }
    }
}