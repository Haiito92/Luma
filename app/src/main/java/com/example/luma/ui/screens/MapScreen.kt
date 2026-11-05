package com.example.luma.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.luma.ui.theme.LumaTheme

@Composable
fun MapScreen(){
    Scaffold() { innerPadding ->
        Column(modifier =
            Modifier.padding(innerPadding)) {

        }
    }
}

@Preview
@Composable
fun MapScreenPreview(){
    LumaTheme() {
        MapScreen()
    }
}