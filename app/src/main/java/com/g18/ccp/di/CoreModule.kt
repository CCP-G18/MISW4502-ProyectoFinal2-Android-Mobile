package com.g18.ccp.di

import com.g18.ccp.data.local.Datasource
import org.koin.dsl.module

val coreModule = module {
    single {
        Datasource(context = get())
    }
}
