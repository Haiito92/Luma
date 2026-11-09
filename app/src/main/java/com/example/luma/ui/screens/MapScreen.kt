package com.example.luma.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.example.luma.viewmodel.MapUiState
import com.example.luma.viewmodel.MapViewModel
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.overlay.CompassButton
import org.maplibre.compose.overlay.ExpandingAttributionButton
import org.maplibre.compose.overlay.MapOverlay
import org.maplibre.compose.overlay.MaplibreLogo
import org.maplibre.compose.overlay.ScaleBar
import org.maplibre.compose.overlay.ZoomButtons
import org.maplibre.compose.style.BaseStyle

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
    val mapLibreState =
        rememberMapState(baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"))

    MaplibreMap(state = mapLibreState,  modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
    ){
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LumaDockedSearchBar(state.query, state.matches, onQueryChange, onMatchSelected)

            Column(modifier = Modifier
                .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                ) {


                    ScaleBar(
                        metersPerDp = { mapLibreState.viewport?.metersPerDpAtCenter ?: 0.0 }, // (1)!
                        modifier = Modifier.align(Alignment.BottomEnd),
                    )
                    CompassButton(modifier = Modifier.align(Alignment.TopEnd))
                    ZoomButtons(Modifier.align(Alignment.CenterEnd))
                    MaplibreLogo(Modifier.align(Alignment.BottomStart))
                    ExpandingAttributionButton(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        contentAlignment = Alignment.BottomEnd,
                    )
                }


                Button(
                    { onDestinationConfirmed() },
                    enabled = state.canConfirm
                ){
                    Text("Confirm")
                }
            }
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