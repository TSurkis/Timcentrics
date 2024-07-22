package com.timcentrics

import android.app.Application
import com.timcentrics.di.ServiceLocatorInitializer
import com.timcentrics.utils.ActivityTracker


class TimcentricsApp : Application() {
    val activityTracker: ActivityTracker = ActivityTracker()

    override fun onCreate() {
        super.onCreate()
        ServiceLocatorInitializer.initialize(this)

        registerActivityLifecycleCallbacks(activityTracker)
    }
}