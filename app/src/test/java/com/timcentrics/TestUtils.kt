package com.timcentrics

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic

fun mockLogger() {
    mockkStatic(Log::class)
    every { Log.d(any(), any()) } returns 0
    every { Log.e(any(), any()) } returns 0
}