package com.example.luma.ui.followSos

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.luma.LumaApplication

val FollowSosViewModelFactory = viewModelFactory {
    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LumaApplication
        FollowSosViewModel(
            app.container.walkRepository
        )
    }
}
