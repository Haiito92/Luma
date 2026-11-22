package com.example.luma.ui.login

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.luma.LumaApplication

val AuthViewModelFactory = viewModelFactory {
    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LumaApplication
        AuthViewModel(
            app.container.authRepository
        )
    }
}
