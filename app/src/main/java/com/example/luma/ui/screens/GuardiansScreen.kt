package com.example.luma.ui.screens

import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.ui.components.LumaAppPreviewScaffold
import com.example.luma.ui.theme.LumaTheme
import com.example.luma.viewmodel.GuardiansViewModel

@Composable
fun GuardiansScreen(viewModel: GuardiansViewModel = viewModel()){
    GuardiansContent()
}

@Composable
fun GuardiansContent(){
    Switch(false, {})
}

@Preview
@Composable
fun GuardiansScreenPreview(){
    LumaTheme() {
        LumaAppPreviewScaffold {
            GuardiansContent()
        }
    }
}