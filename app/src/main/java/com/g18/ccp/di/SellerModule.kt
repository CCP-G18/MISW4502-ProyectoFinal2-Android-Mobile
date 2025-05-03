package com.g18.ccp.di

import com.g18.ccp.core.utils.network.RetrofitProvider
import com.g18.ccp.data.local.model.room.dao.CustomerDao
import com.g18.ccp.data.local.model.room.database.AppDatabase
import com.g18.ccp.data.remote.service.seller.CustomerService
import com.g18.ccp.presentation.seller.customermanagement.SellerCustomerManagementViewModel
import com.g18.ccp.presentation.seller.customerslist.SellerCustomersViewModel
import com.g18.ccp.presentation.seller.home.SellerHomeViewModel
import com.g18.ccp.presentation.seller.personalinfo.SellerCustomerPersonalInfoViewModel
import com.g18.ccp.repository.seller.CustomerRepository
import com.g18.ccp.repository.seller.CustomerRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sellerModule = module {
    single<CustomerService> { get<RetrofitProvider>().instance.create(CustomerService::class.java) }
    single<CustomerDao> { get<AppDatabase>().customerDao() }

    single<CustomerRepository> {
        CustomerRepositoryImpl(
            customerService = get(),
            customerDao = get(),
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
    viewModel<SellerCustomerManagementViewModel> { params ->
        SellerCustomerManagementViewModel(
            savedStateHandle = params.get(),
            customerRepository = get()
        )
    }
    viewModel<SellerCustomerPersonalInfoViewModel> { params ->
        SellerCustomerPersonalInfoViewModel(
            savedStateHandle = params.get(),
            customerRepository = get()
        )
    }
}
