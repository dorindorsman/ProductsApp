package com.example.productsapp.ui.favorites

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

data class FavoritesUiState(
    val favorites: List<Product> = emptyList(),
    val recentlyRemoved: Product? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getFavorites().collect { favorites ->
                _uiState.update { it.copy(favorites = favorites) }
            }
        }
    }

    fun removeFromFavorites(product: Product) {
        viewModelScope.launch {
            repository.toggleFavorite(product.id)
            _uiState.update { it.copy(recentlyRemoved = product) }
        }
    }

    fun undoRemove() {
        viewModelScope.launch {
            _uiState.value.recentlyRemoved?.let {
                repository.toggleFavorite(it.id)
                _uiState.update { state -> state.copy(recentlyRemoved = null) }
            }
        }
    }

    fun clearRecentlyRemoved() {
        _uiState.update { it.copy(recentlyRemoved = null) }
    }
}