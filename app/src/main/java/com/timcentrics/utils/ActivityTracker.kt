package com.timcentrics.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

class ActivityTracker : Application.ActivityLifecycleCallbacks {

    private var currentActivityReference: WeakReference<Activity> = WeakReference(null)
    val currentActivity: Activity? get() = currentActivityReference.get()

    override fun onActivityResumed(currentActivity: Activity) {
        currentActivityReference = WeakReference(currentActivity)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}

    override fun onActivityStarted(p0: Activity) {}

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {}
}