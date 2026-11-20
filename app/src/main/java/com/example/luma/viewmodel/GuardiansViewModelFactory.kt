package com.example.luma.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.luma.LumaApplication

val GuardiansViewModelFactory = viewModelFactory {
    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LumaApplication
        GuardiansViewModel(
            app.container.guardiansRepository
        )
    }
}
