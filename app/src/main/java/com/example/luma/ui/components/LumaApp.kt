package com.example.luma.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.luma.ui.screens.GuardiansScreen
import com.example.luma.ui.screens.MapScreen
import com.example.luma.ui.screens.SettingsScreen

@Composable
fun LumaApp(){
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            LumaNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId){ saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "map",
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            composable("settings") { SettingsScreen() }
            composable("map") { MapScreen() }
            composable("guardians") { GuardiansScreen() }
        }
    }
}

@Composable
fun LumaAppPreviewScaffold(content: @Composable () -> Unit){
    Scaffold(
        bottomBar = {
            LumaNavigationBar(
                currentRoute = "",
                onNavigate = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}