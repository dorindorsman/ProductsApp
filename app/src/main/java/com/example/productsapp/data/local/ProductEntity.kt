package com.example.productsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val thumbnail: String,
    val images: String, // JSON string
    val category: String,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val isLocallyModified: Boolean = false,
    val isFavorite: Boolean = false
)
