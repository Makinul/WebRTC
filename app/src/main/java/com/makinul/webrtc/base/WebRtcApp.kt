package com.makinul.webrtc.base

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WebRtcApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    companion object {
        private const val TAG = "WebRtcApp"

        var instance: WebRtcApp? = null
            private set

        val context: Context?
            get() = instance
    }
}