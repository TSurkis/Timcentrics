package com.timcentrics.di

import com.timcentrics.TimcentricsApp
import com.timcentrics.utils.ConsentCostCalculator
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val utilsModule = module {
    single {
        (androidApplication() as TimcentricsApp).activityTracker
    }

    single {
        ConsentCostCalculator()
    }
}
