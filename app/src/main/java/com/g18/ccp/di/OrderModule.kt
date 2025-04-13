package com.g18.ccp.di

import com.g18.ccp.core.utils.network.RetrofitProvider
import com.g18.ccp.data.remote.service.order.OrderService
import com.g18.ccp.presentation.order.OrdersViewModel
import com.g18.ccp.repository.order.OrdersRepository
import com.g18.ccp.repository.order.OrdersRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val orderModule = module {

    single<OrderService> { get<RetrofitProvider>().instance.create(OrderService::class.java) }

    single<OrdersRepository> {
        OrdersRepositoryImpl(
            orderService = get()
        )
    }

    viewModel<OrdersViewModel>{
        OrdersViewModel(
            repository = get()
        )
    }
}
