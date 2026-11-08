package com.example.luma.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.luma.ui.components.LumaAppPreviewScaffold
import com.example.luma.ui.theme.LumaTheme

@Composable
fun SettingsScreen()
{
    Text("Settings")
    ButtonToggle("Night Mode", {})
}


@Composable
fun ButtonToggle( text : String, onClick: () -> Unit)
{
    Row()
    {
        Text(text)
        //Button(
        //    onClick = onClick,
        //    content = TODO(),
        //)
    }
}


@Preview
@Composable
fun SettingsScreenPreview(){
    LumaTheme() {
        LumaAppPreviewScaffold {
            SettingsScreen()
        }
    }
}