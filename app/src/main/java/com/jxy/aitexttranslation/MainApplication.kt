package com.jxy.aitexttranslation

import android.app.Application
import android.content.Context

class MainApplication : Application() {

    lateinit var globalContext: Context
    override fun onCreate() {
        super.onCreate()
        globalContext = this
    }
}