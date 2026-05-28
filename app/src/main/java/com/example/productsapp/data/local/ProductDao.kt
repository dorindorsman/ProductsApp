package com.example.productsapp.data.local

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("""
        SELECT * FROM products 
        WHERE (:query = '' OR title LIKE '%' || :query || '%')
        ORDER BY 
            CASE WHEN :sortBy = 'price_asc' THEN price END ASC,
            CASE WHEN :sortBy = 'price_desc' THEN price END DESC,
            CASE WHEN :sortBy = 'rating' THEN rating END DESC,
            CASE WHEN :sortBy = 'title' THEN title END ASC,
            id ASC
    """)
    fun getProductsPaged(query: String, sortBy: String): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): ProductEntity?

    @Upsert
    suspend fun upsertProducts(products: List<ProductEntity>)

    @Upsert
    suspend fun upsertProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id AND isLocallyModified = 1")
    suspend fun deleteLocalProduct(id: Int)

    @Query("UPDATE products SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int)

    @Query("DELETE FROM products WHERE isLocallyModified = 0")
    suspend fun clearRemoteProducts()

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCount(): Int
}