package com.example.luma.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.ui.components.LumaAppPreviewScaffold
import com.example.luma.ui.components.LumaDropdownItem
import com.example.luma.ui.components.LumaTextButton
import com.example.luma.ui.components.LumaToggleItem
import com.example.luma.ui.theme.LumaTheme
import com.example.luma.viewmodel.SettingsViewModel
import com.example.luma.viewmodel.SettingsViewModelFactory

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory),
    onLogOutSuccess: () -> Unit = {}
) {
    val shareLocation by viewModel.shareLocation.collectAsStateWithLifecycle()
    val nightMode by viewModel.nightMode.collectAsStateWithLifecycle()
    val selectedColorBlindness by viewModel.selectedColorBlindness.collectAsStateWithLifecycle()

    SettingsContent(
        shareLocation = shareLocation,
        onShareLocationChanged = viewModel::onShareLocationChanged,
        nightMode = nightMode,
        onNightModeChanged = viewModel::onNightModeChanged,
        colorBlindnessOptions = viewModel.colorBlindnessOptions,
        selectedColorBlindness = selectedColorBlindness,
        onColorBlindnessSelected = viewModel::onColorBlindnessSelected,
        onLogOut = { viewModel.onLogOut(onLogOutSuccess) },
        onDeleteAccount = { viewModel.onDeleteAccount(onLogOutSuccess) }
    )
}

@Composable
fun SettingsContent(
    shareLocation: Boolean = true,
    onShareLocationChanged: (Boolean) -> Unit = {},
    nightMode: Boolean = false,
    onNightModeChanged: (Boolean) -> Unit = {},
    colorBlindnessOptions: List<String> = listOf("None", "Protanopia", "Deuteranopia", "Tritanopia"),
    selectedColorBlindness: String = "None",
    onColorBlindnessSelected: (String) -> Unit = {},
    onLogOut: () -> Unit = {},
    onDeleteAccount: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LumaToggleItem(
            title = "Always Share Localisation",
            checked = shareLocation,
            onCheckedChange = onShareLocationChanged
        )

        LumaToggleItem(
            title = "NightMode",
            checked = nightMode,
            onCheckedChange = onNightModeChanged
        )

        LumaDropdownItem(
            title = "Color blindness",
            options = colorBlindnessOptions,
            selectedOption = selectedColorBlindness,
            onOptionSelected = onColorBlindnessSelected
        )

        LumaTextButton(
            text = "Log Out",
            onClick = onLogOut
        )
        LumaTextButton(
            text = "Delete account",
            onClick = onDeleteAccount
        )
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    LumaTheme {
        LumaAppPreviewScaffold {
            SettingsContent()
        }
    }
}
