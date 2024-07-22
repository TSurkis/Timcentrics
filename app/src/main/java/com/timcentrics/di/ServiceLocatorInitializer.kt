package com.timcentrics.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ServiceLocatorInitializer {
    companion object {
        fun initialize(application: Application) {
            startKoin {
                androidLogger()
                androidContext(application.applicationContext)
                modules(
                    listOf(
                        userConsentModule,
                        viewModelModule,
                        utilsModule
                    )
                )
            }
        }
    }
}