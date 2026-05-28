package com.example.productsapp.di

import com.example.productsapp.data.repository.AuthRepositoryImpl
import com.example.productsapp.data.repository.ProductRepositoryImpl
import com.example.productsapp.domain.repository.AuthRepository
import com.example.productsapp.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}