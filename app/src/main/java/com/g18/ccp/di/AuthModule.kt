package com.g18.ccp.di

import com.g18.ccp.presentation.auth.LoginViewModel
import com.g18.ccp.repository.auth.LoginRepository
import com.g18.ccp.repository.auth.LoginRepositoryImpl
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
    viewModel {
        LoginViewModel(loginRepository = get())
    }
}
