package com.example.productsapp.data.local

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("""
    SELECT * FROM products 
    WHERE (:query = '' OR title LIKE '%' || :query || '%')
    AND (:category IS NULL OR category = :category)
    ORDER BY 
        CASE WHEN :sortBy = 'price_asc' THEN price END ASC,
        CASE WHEN :sortBy = 'price_desc' THEN price END DESC,
        CASE WHEN :sortBy = 'rating' THEN rating END DESC,
        CASE WHEN :sortBy = 'title' THEN title END ASC,
        id ASC
""")
    fun getProductsPaged(
        query: String,
        sortBy: String,
        category: String?
    ): PagingSource<Int, ProductEntity>

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

    @Query("SELECT id FROM products WHERE isFavorite = 1")
    suspend fun getFavoriteIds(): List<Int>

    @Query("UPDATE products SET isFavorite = 0")
    suspend fun clearAllFavorites()

    @Query("DELETE FROM products WHERE isLocallyModified = 0 AND isFavorite = 0")
    suspend fun clearRemoteProducts()

    @Query("DELETE FROM products WHERE isLocallyModified = 1")
    suspend fun clearLocalProducts()

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCount(): Int

    @Query("SELECT DISTINCT category FROM products ORDER BY category ASC")
    fun getCategories(): Flow<List<String>>

    @Query("SELECT id FROM products WHERE isLocallyModified = 1")
    suspend fun getLocallyModifiedIds(): List<Int>
}