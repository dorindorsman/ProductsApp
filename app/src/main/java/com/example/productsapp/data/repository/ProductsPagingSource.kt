package com.example.productsapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.productsapp.data.local.ProductDao
import com.example.productsapp.data.local.toDomain
import com.example.productsapp.data.remote.ProductApi
import com.example.productsapp.data.remote.ProductDto
import com.example.productsapp.data.local.toEntity
import com.example.productsapp.domain.model.Product

class ProductsPagingSource(
    private val api: ProductApi,
    private val dao: ProductDao,
    private val query: String,
    private val sortBy: String
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: 0
        val limit = params.loadSize

        return try {
            val response = if (query.isEmpty()) {
                api.getProducts(limit = limit, skip = page * limit)
            } else {
                api.searchProducts(query = query, limit = limit, skip = page * limit)
            }

            // Cache to Room
            dao.upsertProducts(response.products.map { it.toEntity() })

            val products = dao.getProductsPaged(query, sortBy)
                .load(PagingSource.LoadParams.Refresh(page, limit, false))

            when (products) {
                is LoadResult.Page -> LoadResult.Page(
                    data = products.data.map { it.toDomain() },
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (response.products.isEmpty()) null else page + 1
                )
                else -> loadFromCache(page, limit)
            }
        } catch (e: Exception) {
            loadFromCache(page, limit)
        }
    }

    private suspend fun loadFromCache(page: Int, limit: Int): LoadResult<Int, Product> {
        return try {
            val cached = dao.getProductsPaged(query, sortBy)
                .load(PagingSource.LoadParams.Refresh(page, limit, false))
            when (cached) {
                is LoadResult.Page -> LoadResult.Page(
                    data = cached.data.map { it.toDomain() },
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (cached.data.isEmpty()) null else page + 1
                )
                else -> LoadResult.Error(Exception("No cached data"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}