package com.example.productsapp.data.local

import com.example.productsapp.data.remote.ProductDto
import com.example.productsapp.domain.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val gson = Gson()

fun ProductDto.toEntity(): ProductEntity = ProductEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    thumbnail = thumbnail,
    images = gson.toJson(images),
    category = category,
    rating = rating,
    stock = stock,
    brand = brand
)

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    thumbnail = thumbnail,
    images = gson.fromJson(images, object : TypeToken<List<String>>() {}.type),
    category = category,
    rating = rating,
    stock = stock,
    brand = brand,
    isLocallyModified = isLocallyModified,
    isFavorite = isFavorite
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    thumbnail = thumbnail,
    images = gson.toJson(images),
    category = category,
    rating = rating,
    stock = stock,
    brand = brand,
    isLocallyModified = isLocallyModified,
    isFavorite = isFavorite
)