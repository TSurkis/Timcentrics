package com.timcentrics.di

import com.timcentrics.user_consent.IUserConsentManager
import com.timcentrics.user_consent.UserConsentManager
import org.koin.dsl.module

val userConsentModule = module {
    single<IUserConsentManager> {
        UserConsentManager(get(), get())
    }
}