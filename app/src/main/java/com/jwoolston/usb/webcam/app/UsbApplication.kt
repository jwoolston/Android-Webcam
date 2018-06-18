package com.jwoolston.usb.webcam.app

import android.app.Application
import timber.log.Timber

class UsbApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

}