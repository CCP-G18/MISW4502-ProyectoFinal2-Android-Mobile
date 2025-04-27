package com.g18.ccp.di

import com.g18.ccp.core.utils.network.RetrofitProvider
import com.g18.ccp.data.remote.service.seller.CustomerService
import com.g18.ccp.presentation.seller.customerslist.SellerCustomersViewModel
import com.g18.ccp.presentation.seller.home.SellerHomeViewModel
import com.g18.ccp.repository.seller.CustomerRepository
import com.g18.ccp.repository.seller.CustomerRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sellerModule = module {
    single<CustomerService> { get<RetrofitProvider>().instance.create(CustomerService::class.java) }

    single<CustomerRepository> {
        CustomerRepositoryImpl(
            customerService = get(),
        )
    }
    viewModel<SellerHomeViewModel> {
        SellerHomeViewModel(
            userRepository = get(),
        )
    }
    viewModel<SellerCustomersViewModel> {
        SellerCustomersViewModel(
            customerRepository = get(),
        )
    }
}
