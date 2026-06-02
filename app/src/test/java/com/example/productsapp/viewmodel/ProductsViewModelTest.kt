package com.example.productsapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import app.cash.turbine.test
import com.example.productsapp.domain.model.Product
import com.example.productsapp.domain.repository.ProductRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.productsapp.ui.products.ProductsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ProductRepository
    private lateinit var viewModel: ProductsViewModel

    private val testProduct = Product(
        id = 1,
        title = "Test Product",
        description = "Description",
        price = 9.99,
        thumbnail = "",
        images = emptyList(),
        category = "electronics",
        rating = 4.5,
        stock = 10,
        brand = "Brand"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        every { repository.getProducts(any(), any(), any()) } returns flowOf(PagingData.empty())
        every { repository.getCategories() } returns flowOf(emptyList())
        viewModel = ProductsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(false, state.showAddEditDialog)
            assertNull(state.editingProduct)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChange updates search query`() = runTest {
        viewModel.onSearchQueryChange("laptop")

        viewModel.searchQuery.test {
            assertEquals("laptop", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSortChange updates sort`() = runTest {
        viewModel.onSortChange("price_asc")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("price_asc", state.sortBy)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `showAddDialog sets showAddEditDialog to true`() = runTest {
        viewModel.showAddDialog()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.showAddEditDialog)
            assertNull(state.editingProduct)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `showEditDialog sets product and showAddEditDialog to true`() = runTest {
        viewModel.showEditDialog(testProduct)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.showAddEditDialog)
            assertEquals(testProduct, state.editingProduct)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `hideDialog resets dialog state`() = runTest {
        viewModel.showAddDialog()
        viewModel.hideDialog()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(false, state.showAddEditDialog)
            assertNull(state.editingProduct)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite calls repository`() = runTest {
        viewModel.toggleFavorite(1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.toggleFavorite(1) }
    }

    @Test
    fun `deleteProduct calls repository`() = runTest {
        viewModel.deleteProduct(1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.deleteProduct(1) }
    }

    @Test
    fun `resetLocalChanges calls repository`() = runTest {
        viewModel.resetLocalChanges()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.resetLocalChanges() }
    }

    @Test
    fun `onCategorySelected updates selectedCategory`() = runTest {
        viewModel.onCategorySelected("electronics")

        viewModel.selectedCategory.test {
            assertEquals("electronics", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}