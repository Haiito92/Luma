package com.example.luma.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.ui.components.LumaAppPreviewScaffold
import com.example.luma.ui.components.LumaDockedSearchBar
import com.example.luma.ui.theme.LumaTheme
import com.example.luma.viewmodel.MapUiState
import com.example.luma.viewmodel.MapViewModel

@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()){

    val state by viewModel.state.collectAsStateWithLifecycle()

    MapContent(
        state,
        viewModel::onQueryChange,
        viewModel::onMatchSelected,
        viewModel::onDestinationConfirmed)
}

@Composable
fun MapContent(
    state: MapUiState,
    onQueryChange: (String) -> Unit,
    onMatchSelected: (String) -> Unit,
    onDestinationConfirmed: () -> Unit
){
    when(state){
        is MapUiState.SelectingDestination -> {
            DestinationSelection(state, onQueryChange, onMatchSelected, onDestinationConfirmed)
        }
        is MapUiState.SelectingGuardians -> {
            GuardiansSelection()
        }
    }
}

@Composable
fun DestinationSelection(
    state: MapUiState.SelectingDestination,
    onQueryChange: (String) -> Unit,
    onMatchSelected: (String) -> Unit,
    onDestinationConfirmed: () -> Unit
){
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LumaDockedSearchBar(state.query, state.matches, onQueryChange, onMatchSelected)

        Button({ onDestinationConfirmed() }, enabled = state.canConfirm){
            Text("Confirm")
        }
    }
}

@Composable
fun GuardiansSelection(){
    Button({ }){
        Text("Test")
    }
}


@Preview
@Composable
fun MapScreenPreview(){
    LumaTheme() {
        LumaAppPreviewScaffold {
            MapContent(
                MapUiState.SelectingDestination("", listOf("Home", "Pool", "Gym", "Graveyard", "School", "Shooting Range", "Apothecary", "Zoo")),
                {},
                {},
                {})
        }
    }
}