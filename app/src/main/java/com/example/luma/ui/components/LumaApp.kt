package com.example.luma.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.luma.ui.screens.AddGuardiansScreen
import com.example.luma.ui.screens.FollowingCourseScreen
import com.example.luma.ui.screens.FollowingScreen
import com.example.luma.ui.screens.GuardiansScreen
import com.example.luma.ui.map.MapScreen
import com.example.luma.ui.screens.SettingsScreen
import com.example.luma.ui.theme.LumaTheme
import com.example.luma.viewmodel.FollowingViewModel
import com.example.luma.viewmodel.GuardiansTab
import com.example.luma.viewmodel.GuardiansViewModel
import com.example.luma.viewmodel.SettingsViewModel

@Composable
fun LumaApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val settingsViewModel: SettingsViewModel = viewModel()
    val guardiansViewModel: GuardiansViewModel = viewModel()
    val followingViewModel: FollowingViewModel = viewModel()

    val nightMode by settingsViewModel.nightMode.collectAsStateWithLifecycle()

    LumaTheme(darkTheme = nightMode) {
        Scaffold(
            bottomBar = {
                LumaNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (route == "guardians") {
                            guardiansViewModel.onTabSelected(GuardiansTab.Guardians)
                        }
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = false
                            }
                            launchSingleTop = true
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
                    .fillMaxWidth()
            ) {
                composable("settings") {
                    SettingsScreen(viewModel = settingsViewModel)
                }
                composable("map") {
                    MapScreen()
                }
                composable("guardians") {
                    GuardiansScreen(
                        viewModel = guardiansViewModel,
                        onAddGuardiansClick = { navController.navigate("add_guardians") },
                        onGuardedContactClick = { name ->
                            val person = followingViewModel.followedPeople.value
                                .firstOrNull { it.name == name }
                            if (person != null) {
                                followingViewModel.onPersonSelected(person)
                            }
                            navController.navigate("following_course")
                        }
                    )
                }
                composable("add_guardians") {
                    AddGuardiansScreen(
                        viewModel = guardiansViewModel,
                        onGuardianAdded = { navController.popBackStack() }
                    )
                }
                composable("following") {
                    FollowingScreen(
                        viewModel = followingViewModel,
                        onPersonClick = { navController.navigate("following_course") }
                    )
                }
                composable("following_course") {
                    FollowingCourseScreen(viewModel = followingViewModel)
                }
            }
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
