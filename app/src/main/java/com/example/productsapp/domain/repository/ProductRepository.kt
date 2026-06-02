package com.example.productsapp.domain.repository

import androidx.paging.PagingData
import com.example.productsapp.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(query: String, sortBy: String, category: String?): Flow<PagingData<Product>>
    fun getFavorites(): Flow<List<Product>>
    suspend fun getProductById(id: Int): Product?
    suspend fun addOrUpdateProduct(product: Product)
    suspend fun deleteProduct(id: Int)
    suspend fun resetLocalChanges()
    suspend fun toggleFavorite(productId: Int)

    fun getCategories(): Flow<List<String>>

}