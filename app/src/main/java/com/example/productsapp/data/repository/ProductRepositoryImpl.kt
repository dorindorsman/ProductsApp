package com.example.productsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.productsapp.data.local.ProductDao
import com.example.productsapp.data.local.toDomain
import com.example.productsapp.data.local.toEntity
import com.example.productsapp.data.remote.ProductApi
import com.example.productsapp.domain.model.Product
import com.example.productsapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi,
    private val dao: ProductDao
) : ProductRepository {

    override fun getProducts(query: String, sortBy: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ProductsPagingSource(api, dao, query, sortBy)
            }
        ).flow
    }

    override fun getFavorites(): Flow<List<Product>> {
        return dao.getFavorites().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getProductById(id: Int): Product? {
        return dao.getProductById(id)?.toDomain()
    }

    override suspend fun addOrUpdateProduct(product: Product) {
        dao.upsertProduct(product.toEntity().copy(isLocallyModified = true))
    }

    override suspend fun deleteProduct(id: Int) {
        dao.deleteLocalProduct(id)
    }

    override suspend fun resetLocalChanges() {
        dao.clearRemoteProducts()
    }

    override suspend fun toggleFavorite(productId: Int) {
        dao.toggleFavorite(productId)
    }
}