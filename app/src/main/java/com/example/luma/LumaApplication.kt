package com.example.luma

import android.app.Application
import com.example.luma.data.AppContainer

class LumaApplication: Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}