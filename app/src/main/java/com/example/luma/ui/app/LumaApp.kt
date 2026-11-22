package com.example.luma.ui.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.luma.ui.components.LumaNavigationBar
import com.example.luma.ui.guardians.GuardiansScreen
import com.example.luma.ui.login.LoginScreen
import com.example.luma.ui.map.MapScreen
import com.example.luma.ui.settings.SettingsScreen
import com.example.luma.ui.login.SignUpScreen

@Composable
fun LumaApp(appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory)) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != null && currentRoute != "login" && currentRoute != "signup") {
                LumaNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("map") {
                                inclusive = false
                            }
                            launchSingleTop = route != "guardians"
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("map") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = {
                        navController.navigate("signup")
                    }
                )
            }
            composable("signup") {
                SignUpScreen(
                    onSignUpSuccess = {
                        navController.navigate("map") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable("settings") {
                SettingsScreen(
                    onLogOutSuccess = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable("map") { MapScreen() }
            composable("guardians") { GuardiansScreen() }
        }
    }
}

@Composable
fun LumaAppPreviewScaffold(content: @Composable () -> Unit) {
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
