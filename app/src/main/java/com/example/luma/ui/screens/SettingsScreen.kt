package com.example.luma.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.luma.ui.theme.LumaTheme

@Composable
fun SettingsScreen()
{
    Scaffold()
    {innerPadding ->
        Column(modifier = Modifier.padding(innerPadding))
        {
            Text("Settings")
            ButtonToggle("Night Mode", {})
        }
    }
}


@Composable
fun ButtonToggle( text : String, onClick: () -> Unit)
{
    Row()
    {
        Text(text)
        Button(
            onClick = onClick,
            content = TODO(),
        )
    }
}


@Preview
@Composable
fun SettingsScreenPreview(){
    LumaTheme() {
        SettingsScreen()
    }
}