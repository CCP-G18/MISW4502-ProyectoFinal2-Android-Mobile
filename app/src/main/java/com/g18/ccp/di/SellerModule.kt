package com.g18.ccp.di

import com.g18.ccp.core.utils.network.RetrofitProvider
import com.g18.ccp.data.local.model.room.dao.CategoryDao
import com.g18.ccp.data.local.model.room.dao.CustomerDao
import com.g18.ccp.data.local.model.room.dao.SellerCartDao
import com.g18.ccp.data.local.model.room.dao.SellerProductDao
import com.g18.ccp.data.local.model.room.database.AppDatabase
import com.g18.ccp.data.remote.service.seller.CustomerService
import com.g18.ccp.data.remote.service.seller.order.SellerCustomerOrderService
import com.g18.ccp.data.remote.service.seller.order.category.CategoryService
import com.g18.ccp.data.remote.service.seller.order.product.SellerProductService
import com.g18.ccp.data.remote.service.seller.visits.VisitService
import com.g18.ccp.presentation.seller.customermanagement.SellerCustomerManagementViewModel
import com.g18.ccp.presentation.seller.customerslist.SellerCustomersViewModel
import com.g18.ccp.presentation.seller.customervisit.list.SellerCustomerVisitsViewModel
import com.g18.ccp.presentation.seller.customervisit.register.SellerRegisterVisitViewModel
import com.g18.ccp.presentation.seller.home.SellerHomeViewModel
import com.g18.ccp.presentation.seller.order.category.CategoryViewModel
import com.g18.ccp.presentation.seller.order.category.products.SellerCategoryProductsViewModel
import com.g18.ccp.presentation.seller.order.category.products.cart.SellerCartViewModel
import com.g18.ccp.presentation.seller.order.createorder.SellerCustomerOrdersViewModel
import com.g18.ccp.presentation.seller.personalinfo.SellerCustomerPersonalInfoViewModel
import com.g18.ccp.presentation.seller.recommendation.SellerCustomerRecommendationsViewModel
import com.g18.ccp.repository.seller.CustomerRepository
import com.g18.ccp.repository.seller.CustomerRepositoryImpl
import com.g18.ccp.repository.seller.customervisit.VisitRepository
import com.g18.ccp.repository.seller.customervisit.VisitRepositoryImpl
import com.g18.ccp.repository.seller.order.cart.SellerCartRepository
import com.g18.ccp.repository.seller.order.cart.SellerCartRepositoryImpl
import com.g18.ccp.repository.seller.order.cart.SellerOrderRepository
import com.g18.ccp.repository.seller.order.cart.SellerOrderRepositoryImpl
import com.g18.ccp.repository.seller.order.category.SellerCategoryRepository
import com.g18.ccp.repository.seller.order.category.SellerCategoryRepositoryImpl
import com.g18.ccp.repository.seller.order.category.product.SellerProductRepository
import com.g18.ccp.repository.seller.order.category.product.SellerProductRepositoryImpl
import com.g18.ccp.repository.seller.order.createorder.SellerCustomerOrdersRepository
import com.g18.ccp.repository.seller.order.createorder.SellerCustomerOrdersRepositoryImpl
import com.g18.ccp.repository.seller.videorecommendation.VideoRepository
import com.g18.ccp.repository.seller.videorecommendation.VideoRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sellerModule = module {
    single<CustomerService> { get<RetrofitProvider>().instance.create(CustomerService::class.java) }
    single<CustomerDao> { get<AppDatabase>().customerDao() }

    single<CategoryService> { get<RetrofitProvider>().instance.create(CategoryService::class.java) }
    single<CategoryDao> { get<AppDatabase>().categoryDao() }
    single<SellerProductDao> { get<AppDatabase>().sellerProductDao() }
    single<SellerCartDao> { get<AppDatabase>().sellerCartDao() }

    single<CustomerRepository> {
        CustomerRepositoryImpl(
            customerService = get(),
            customerDao = get(),
        )
    }
    single<VideoRepository> { VideoRepositoryImpl(androidContext()) }
    single<VisitService> { get<RetrofitProvider>().instance.create(VisitService::class.java) }
    single<VisitRepository> { VisitRepositoryImpl(datasource = get(), visitApiService = get()) }
    single<SellerCategoryRepository> {
        SellerCategoryRepositoryImpl(
            categoryService = get(),
            categoryDao = get(),
        )
    }
    single<SellerProductService> { get<RetrofitProvider>().instance.create(SellerProductService::class.java) }
    single<SellerProductRepository> {
        SellerProductRepositoryImpl(
            productApiService = get(),
            productDao = get(),
        )
    }
    single<SellerCartRepository> {
        SellerCartRepositoryImpl(
            cartDao = get(),
            productRepository = get(),
        )
    }
    single<SellerOrderRepository> {
        SellerOrderRepositoryImpl(
            productApiService = get(),
        )
    }
    single<SellerCustomerOrderService> {
        get<RetrofitProvider>().instance.create(SellerCustomerOrderService::class.java)
    }
    single<SellerCustomerOrdersRepository> {
        SellerCustomerOrdersRepositoryImpl(
            service = get(),
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
    viewModel<SellerCustomerVisitsViewModel> { params ->
        SellerCustomerVisitsViewModel(
            savedStateHandle = params.get(),
            visitRepository = get(),
            customerRepository = get(),
            userRepository = get(),
        )
    }
    viewModel<SellerRegisterVisitViewModel> { params ->
        SellerRegisterVisitViewModel(
            savedStateHandle = params.get(),
            visitRepository = get(),
            customerRepository = get(),
        )
    }
    viewModel { params ->
        SellerCustomerRecommendationsViewModel(
            savedStateHandle = params.get(),
            videoRepository = get()
        )
    }
    viewModel { params ->
        CategoryViewModel(
            categoryRepository = get(),
            savedStateHandle = params.get(),
        )
    }
    viewModel { params ->
        SellerCategoryProductsViewModel(
            productRepository = get(),
            cartRepository = get(),
            savedStateHandle = params.get(),
        )
    }
    viewModel { params ->
        SellerCartViewModel(
            orderRepository = get(),
            cartRepository = get(),
            savedStateHandle = params.get(),
        )
    }
    viewModel { params ->
        SellerCustomerOrdersViewModel(
            repo = get(),
            savedStateHandle = params.get(),
        )
    }
}
