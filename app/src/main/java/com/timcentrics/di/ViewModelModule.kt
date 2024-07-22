package com.timcentrics.di

import com.timcentrics.screens.user_consent_screen.UserConsentCostViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        UserConsentCostViewModel(get(), get())
    }
}