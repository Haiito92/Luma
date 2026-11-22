package com.example.luma.ui.settings

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.luma.LumaApplication

val SettingsViewModelFactory = viewModelFactory {
    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LumaApplication
        SettingsViewModel(
            app.container.settingsRepository,
            app.container.authRepository
        )
    }
}
