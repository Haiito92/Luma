package com.example.luma.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.ui.components.LumaAppPreviewScaffold
import com.example.luma.ui.components.LumaDockedSearchBar
import com.example.luma.ui.theme.LumaTheme
import com.example.luma.viewmodel.MapState
import com.example.luma.viewmodel.MapViewModel

@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()){

    val state by viewModel.state.collectAsStateWithLifecycle()
    val places = listOf("Home", "Pool", "Gym", "Graveyard", "School", "Shooting Range", "Apothecary", "Zoo")

    MapContent(state, places, viewModel::onDestinationConfirmed)
}

@Composable
fun MapContent(state: MapState, destinations: List<String>, onDestinationConfirmed: () -> Unit){
    when(state){
        is MapState.SelectingDestination -> {
            DestinationSelection(destinations, onDestinationConfirmed)
        }
        is MapState.SelectingGuardians -> {
            GuardiansSelection()
        }
    }
}

@Composable
fun DestinationSelection(destinations: List<String>, onDestinationConfirmed: () -> Unit){

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LumaDockedSearchBar(destinations)

        Button({ onDestinationConfirmed() }){
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
    val places = listOf("Home", "Pool", "Gym", "Graveyard", "School", "Shooting Range", "Apothecary", "Zoo")

    LumaTheme() {
        LumaAppPreviewScaffold {
            MapContent(MapState.SelectingDestination, places, { })
        }
    }
}