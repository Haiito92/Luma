package com.example.luma.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.data.LatLng
import com.example.luma.data.Place
import com.example.luma.data.UserLocation
import com.example.luma.ui.components.LumaAppPreviewScaffold
import com.example.luma.ui.components.LumaDockedSearchBar
import com.example.luma.ui.theme.LumaTheme
import com.example.luma.viewmodel.MapUiState
import com.example.luma.viewmodel.MapViewModel
import com.example.luma.viewmodel.MapViewModelFactory
import org.maplibre.compose.camera.CameraUpdate
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.map.MapState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.overlay.CompassButton
import org.maplibre.compose.overlay.ExpandingAttributionButton
import org.maplibre.compose.overlay.MapOverlay
import org.maplibre.compose.overlay.MaplibreLogo
import org.maplibre.compose.overlay.ScaleBar
import org.maplibre.compose.overlay.ZoomButtons
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position

@Composable
fun MapScreen(mapViewModel: MapViewModel = viewModel(factory = MapViewModelFactory)){

    val state by mapViewModel.state.collectAsStateWithLifecycle()
    val userLocation by mapViewModel.userLocation.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val granted = results.values.any { it }      // fine OR coarse is enough
        mapViewModel.onLocationPermissionResult(granted)
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val alreadyGranted = permissions.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (alreadyGranted) mapViewModel.onLocationPermissionResult(true)
        else permissionLauncher.launch(permissions)
    }

    MapContent(
        state,
        userLocation,
        mapViewModel::onQueryChange,
        mapViewModel::onMatchSelected,
        mapViewModel::onDestinationConfirmed)
}

@Composable
fun MapContent(
    state: MapUiState,
    userLocation: UserLocation?,
    onQueryChange: (String) -> Unit,
    onMatchSelected: (Place) -> Unit,
    onDestinationConfirmed: () -> Unit
){
    val mapLibreState =
        rememberMapState(baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")
        ){
            val userSource = rememberGeoJsonSource(
                GeoJsonData.JsonString(userLocation.toGeoJson())
            )
            CircleLayer(id = "user-dot-outline", source = userSource,
                radius = const(10.dp), color = const(MaterialTheme.colorScheme.background))
            CircleLayer(id = "user-dot", source = userSource,
                radius = const(7.dp), color = const(MaterialTheme.colorScheme.primary))
        }

    // We use this key to track when we get the first location,
    // we move cam on first location received
    val hasLocation = userLocation != null
    LaunchedEffect(mapLibreState, hasLocation) {
        userLocation?.let { userLocation ->
            mapLibreState.animateCamera(
                update = CameraUpdate(
                    center = Position(latitude = userLocation.latLng.latitude, longitude = userLocation.latLng.longitude),
                    zoom = 15.0
                )
            )
        }
    }

    MaplibreMap(state = mapLibreState,  modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
    ){
        when(state){
            is MapUiState.SelectingDestination -> {
                DestinationSelection(
                    state,
                    mapLibreState,
                    onQueryChange,
                    onMatchSelected,
                    onDestinationConfirmed)
            }
            is MapUiState.SelectingGuardians -> {
                GuardiansSelection()
            }
        }
    }
}

@Composable
fun DestinationSelection(
    state: MapUiState.SelectingDestination,
    mapLibreState: MapState,
    onQueryChange: (String) -> Unit,
    onMatchSelected: (Place) -> Unit,
    onDestinationConfirmed: () -> Unit
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
                MapUiState.SelectingDestination("", listOf(Place("Home", LatLng(0.0, 0.0)), Place("Gym",
                    LatLng(0.0 ,0.0)))),
                UserLocation(LatLng(0.0,0.0), 0.0f),
                {},
                {},
                {})
        }
    }
}

private fun UserLocation?.toGeoJson(): String {
    if (this == null) {
        return """{"type":"FeatureCollection","features":[]}"""
    } else {
        return """{"type":"Feature","geometry":{"type":"Point","coordinates":[${latLng.longitude},${latLng.latitude}]},"properties":{}}"""
    }
}
