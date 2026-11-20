package com.example.luma.data

import android.content.Context

class AppContainer(context: Context) {

    val mapRepository = MapRepository()

    val locationRepository = LocationRepository(context.applicationContext)

    val authRepository = AuthRepository()

    val settingsRepository = SettingsRepository()

    val guardiansRepository = GuardiansRepository()
}
