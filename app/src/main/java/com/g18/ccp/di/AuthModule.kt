package com.g18.ccp.di

import com.g18.ccp.presentation.auth.LoginViewModel
import com.g18.ccp.presentation.auth.RegisterClientViewModel
import com.g18.ccp.repository.auth.LoginRepository
import com.g18.ccp.repository.auth.LoginRepositoryImpl
import com.g18.ccp.repository.auth.register.client.ClientRegisterRepository
import com.g18.ccp.repository.auth.register.client.ClientRegisterRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<LoginRepository> {
        LoginRepositoryImpl(
            authService = get(),
            authenticationManager = get(),
            datasource = get()
        )
    }
    single<ClientRegisterRepository> {
        ClientRegisterRepositoryImpl(
            api = get(),
        )
    }
    viewModel {
        LoginViewModel(loginRepository = get())
    }
    viewModel {
        RegisterClientViewModel(
            registerClientRepository = get()
        )
    }
}
