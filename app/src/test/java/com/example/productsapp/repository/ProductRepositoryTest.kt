package com.example.productsapp.repository

import com.example.productsapp.data.local.ProductDao
import com.example.productsapp.data.local.ProductEntity
import com.example.productsapp.data.remote.ProductApi
import com.example.productsapp.data.repository.ProductRepositoryImpl
import com.example.productsapp.domain.model.Product
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProductRepositoryTest {

    private lateinit var api: ProductApi
    private lateinit var dao: ProductDao
    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        dao = mockk(relaxed = true)
        repository = ProductRepositoryImpl(api, dao)
    }

    @Test
    fun `getProductById returns null when not found`() = runTest {
        // Given
        coEvery { dao.getProductById(999) } returns null

        // When
        val result = repository.getProductById(999)

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `getProductById returns product when found`() = runTest {
        // Given
        val entity = ProductEntity(
            id = 1,
            title = "Test Product",
            description = "Description",
            price = 9.99,
            thumbnail = "url",
            images = "[]",
            category = "test",
            rating = 4.5,
            stock = 10,
            brand = "Brand"
        )
        coEvery { dao.getProductById(1) } returns entity

        // When
        val result = repository.getProductById(1)

        // Then
        assertEquals(1, result?.id)
        assertEquals("Test Product", result?.title)
    }

    @Test
    fun `addOrUpdateProduct calls upsert with isLocallyModified true`() = runTest {
        // Given
        val product = Product(
            id = 1,
            title = "Test",
            description = "Desc",
            price = 9.99,
            thumbnail = "",
            images = emptyList(),
            category = "test",
            rating = 4.0,
            stock = 5,
            brand = null
        )

        // When
        repository.addOrUpdateProduct(product)

        // Then
        coVerify {
            dao.upsertProduct(match { it.isLocallyModified })
        }
    }

    @Test
    fun `toggleFavorite calls dao toggleFavorite`() = runTest {
        // When
        repository.toggleFavorite(1)

        // Then
        coVerify { dao.toggleFavorite(1) }
    }

    @Test
    fun `deleteProduct calls dao deleteLocalProduct`() = runTest {
        // When
        repository.deleteProduct(1)

        // Then
        coVerify { dao.deleteLocalProduct(1) }
    }
}