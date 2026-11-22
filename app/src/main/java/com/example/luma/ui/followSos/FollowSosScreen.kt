package com.example.luma.ui.followSos

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.ui.app.LumaAppPreviewScaffold
import com.example.luma.ui.theme.LumaTheme
import com.example.luma.utils.toBoundingBox
import com.example.luma.utils.toGeoJson
import com.example.luma.utils.toPointGeoJson
import org.maplibre.compose.camera.CameraFit
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.DpPadding

@Composable
fun FollowSosScreen(
    walkId: String,
    viewModel: FollowSosViewModel = viewModel(factory = FollowSosViewModelFactory)
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(walkId) {
        viewModel.observeWalk(walkId)
    }

    FollowSosContent(state)
}

@Composable
fun FollowSosContent(
    state: FollowSosUiState
) {

    val walk = state.walkOrNull()
    val mapLibreState =
        rememberMapState(baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")
        ){
            if(walk != null){
                // Route layer
                val routeSource = rememberGeoJsonSource(
                    GeoJsonData.JsonString(walk.route.toGeoJson())
                )

                LineLayer(id = "route", source = routeSource,
                    width = const(4.dp), color = const(MaterialTheme.colorScheme.primary))

                // destination pin

                val destinationSource = rememberGeoJsonSource(
                    GeoJsonData.JsonString(walk.destination.latLng.toPointGeoJson())
                )

                CircleLayer(id = "destination-dot-outline", source = destinationSource,
                    radius = const(10.dp), color = const(MaterialTheme.colorScheme.background))
                CircleLayer(id = "destination-dot", source = destinationSource,
                    radius = const(7.dp), color = const(MaterialTheme.colorScheme.tertiary))

                // user location dots
                val walkerSource = rememberGeoJsonSource(
                    GeoJsonData.JsonString(walk.walkerLocation.toPointGeoJson())
                )
                CircleLayer(id = "user-dot-outline", source = walkerSource,
                    radius = const(10.dp), color = const(MaterialTheme.colorScheme.background))
                CircleLayer(id = "user-dot", source = walkerSource,
                    radius = const(7.dp), color = const(MaterialTheme.colorScheme.primary))
            }
        }

    LaunchedEffect(mapLibreState, walk) {
        if (walk != null &&  walk.route.points.size >= 2) {
            mapLibreState.animateCameraToBounds(
                boundingBox = walk.route.toBoundingBox(),
                fit = CameraFit(
                    fitPadding = DpPadding(
                        left = 32.dp,
                        top = 120.dp,
                        right = 32.dp,
                        bottom = 220.dp
                    )
                )
            )
        }
    }

    MaplibreMap(state = mapLibreState,  modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
    ){

    }
}



@Preview
@Composable
fun FollowingCourseScreenPreview() {
    LumaTheme {
        LumaAppPreviewScaffold {
            FollowSosContent(state = FollowSosUiState.Loading)
        }
    }
}
