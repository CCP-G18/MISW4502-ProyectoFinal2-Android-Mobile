package com.g18.ccp.di

import com.g18.ccp.core.utils.network.RetrofitProvider
import com.g18.ccp.data.remote.service.product.ProductService
import com.g18.ccp.presentation.order.create.ListProductViewModel
import com.g18.ccp.repository.product.ProductRepository
import com.g18.ccp.repository.product.ProductRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val productModule = module {
    single<ProductService> { get<RetrofitProvider>().instance.create(ProductService::class.java) }

    single<ProductRepository> {
        ProductRepositoryImpl(
            productService = get()
        )
    }
    viewModel<ListProductViewModel> {
        ListProductViewModel(
            productRepository = get(),
            orderRepository = get(),
        )
    }
}
