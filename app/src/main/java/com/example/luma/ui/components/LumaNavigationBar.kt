package com.example.luma.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable

@Composable
fun LumaNavigationBar(currentRoute: String?, onNavigate: (String) -> Unit){
    NavigationBar(){
        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick = { onNavigate("settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Guardians")},
        )
        NavigationBarItem(
            selected = currentRoute == "map",
            onClick = { onNavigate("map") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home")},
        )
        NavigationBarItem(
            selected = currentRoute == "guardians",
            onClick = { onNavigate("guardians") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Settings")},
        )
    }
}