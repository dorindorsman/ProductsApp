package com.example.productsapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.productsapp.data.local.ProductDao
import com.example.productsapp.data.local.ProductEntity
import com.example.productsapp.data.local.toEntity
import com.example.productsapp.data.remote.ProductApi
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ProductsRemoteMediator @Inject constructor(
    private val api: ProductApi,
    private val dao: ProductDao
) : RemoteMediator<Int, ProductEntity>() {

    private var currentPage = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    currentPage = 0
                    0
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    currentPage++
                    currentPage
                }
            }

            val limit = state.config.pageSize
            val response = api.getProducts(limit = limit, skip = page * limit)

            if (loadType == LoadType.REFRESH) {
                dao.clearRemoteProducts()
            }

            val existingFavorites = dao.getFavoriteIds()
            val locallyModifiedIds = dao.getLocallyModifiedIds()

            val products = response.products
                .filter { it.id !in locallyModifiedIds }
                .map { dto ->
                    dto.toEntity().copy(
                        isFavorite = dto.id in existingFavorites
                    )
                }

            dao.upsertProducts(products)

            MediatorResult.Success(endOfPaginationReached = response.products.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}