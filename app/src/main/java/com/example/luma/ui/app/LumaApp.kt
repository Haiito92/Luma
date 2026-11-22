package com.example.luma.ui.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.luma.data.local.auth.AuthState
import com.example.luma.ui.components.LumaNavigationBar
import com.example.luma.ui.guardians.GuardiansScreen
import com.example.luma.ui.auth.LoginScreen
import com.example.luma.ui.map.MapScreen
import com.example.luma.ui.settings.SettingsScreen
import com.example.luma.ui.auth.SignUpScreen

@Composable
fun LumaApp(appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory)) {
    val authState by appViewModel.authState.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(authState) {
        when (authState) {
            AuthState.Loading -> {
                navController.navigate("loading") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            AuthState.Unauthenticated -> {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            is AuthState.Authenticated -> {
                navController.navigate("map") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            is AuthState.Error -> {
                // We'll handle profile-loading errors separately.
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute != null && currentRoute != "loading" && currentRoute != "login" && currentRoute != "signup") {
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
            startDestination = "loading",
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            composable("loading") {

            }
            composable("login") {
                LoginScreen(
                    onNavigateToSignUp = {
                        navController.navigate("signup")
                    }
                )
            }
            composable("signup") {
                SignUpScreen(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable("settings") {
                SettingsScreen()
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
