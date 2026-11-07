package com.example.luma.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.ui.theme.LumaTheme
import com.example.luma.viewmodel.GuardiansViewModel

@Composable
fun GuardiansScreen(viewModel: GuardiansViewModel = viewModel()){
    GuardiansContent()
}

@Composable
fun GuardiansContent(){

}

@Composable
fun GuardiansPreview(){
    LumaTheme() {
        GuardiansContent()
    }
}