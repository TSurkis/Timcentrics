package com.timcentrics.user_consent

import android.content.Context
import android.util.Log
import com.timcentrics.BuildConfig
import com.timcentrics.utils.ActivityTracker
import com.usercentrics.sdk.Usercentrics
import com.usercentrics.sdk.UsercentricsBanner
import com.usercentrics.sdk.UsercentricsConsentUserResponse
import com.usercentrics.sdk.UsercentricsOptions
import com.usercentrics.sdk.errors.UsercentricsError

interface IUserConsentManager {
    fun display(
        response: (UsercentricsConsentUserResponse?) -> Unit,
        errorResponse: (UsercentricsError?) -> Unit
    )
}

class UserConsentManager(
    private val appContext: Context,
    private val activityTracker: ActivityTracker
) : IUserConsentManager {

    private var didInitialize: Boolean = false

    override fun display(
        response: (UsercentricsConsentUserResponse?) -> Unit,
        errorResponse: (UsercentricsError?) -> Unit,
    ) {
        val usercentricsOptions =
            UsercentricsOptions(settingsId = BuildConfig.USERCENTRICS_SETTINGS_ID)

        if (didInitialize) {
            displayConsentBanner(response)
        } else {
            Usercentrics.initialize(appContext, usercentricsOptions)
            Usercentrics.isReady(
                onSuccess = { status ->
                    didInitialize = true

                    displayConsentBanner(response)
                },
                onFailure = errorResponse
            )
        }
    }

    private fun displayConsentBanner(response: (UsercentricsConsentUserResponse?) -> Unit) {
        activityTracker.currentActivity?.let { activity ->
            UsercentricsBanner(activity).showSecondLayer(response)
        } ?: run {
            Log.e("UserConsentManager", "Couldn't detect a live activity to display consent screen")
        }
    }
}