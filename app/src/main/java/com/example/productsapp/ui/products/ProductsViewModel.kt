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

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    val categories: StateFlow<List<String>> = repository.getCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortBy = MutableStateFlow("default")

    private val _refreshTrigger = MutableStateFlow(0)

    val products: Flow<PagingData<Product>> = combine(
        _searchQuery, _sortBy, _selectedCategory, _refreshTrigger
    ) { query, sort, category, _ ->
        Triple(query, sort, category)
    }
        .debounce(300)
        .flatMapLatest { (query, sort, category) ->
            repository.getProducts(query, sort, category)
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
            _refreshTrigger.value++
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            _refreshTrigger.value++
        }
    }

    fun resetLocalChanges() {
        viewModelScope.launch {
            repository.resetLocalChanges()
            _refreshTrigger.value++
        }
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }
}