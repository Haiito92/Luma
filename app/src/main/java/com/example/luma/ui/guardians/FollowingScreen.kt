package com.example.luma.ui.guardians

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.ui.app.LumaAppPreviewScaffold
import com.example.luma.ui.theme.LumaTheme

@Composable
fun FollowingScreen(
    viewModel: FollowingViewModel = viewModel(factory = FollowingViewModelFactory),
    onPersonClick: (FollowedPerson) -> Unit = {}
) {
    val followedPeople by viewModel.followedPeople.collectAsStateWithLifecycle()

    FollowingContent(
        followedPeople = followedPeople,
        onPersonClick = { person ->
            viewModel.onPersonSelected(person)
            onPersonClick(person)
        }
    )
}

@Composable
fun FollowingContent(
    followedPeople: List<FollowedPerson>,
    onPersonClick: (FollowedPerson) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(followedPeople, key = { it.id }) { person ->
                //ContactItem(
                //    name = person.name,
                //    onClick = { onPersonClick(person) }
                //)
            }
        }
    }
}

@Preview
@Composable
fun FollowingScreenPreview() {
    val samplePeople = listOf(
        FollowedPerson(id = "1", name = "Jean-Baptiste Michelin", remainingMinutes = 5),
        FollowedPerson(id = "2", name = "Jean-Baptiste Michelin", remainingMinutes = 12),
        FollowedPerson(id = "3", name = "Jean-Baptiste Michelin", remainingMinutes = 20)
    )

    LumaTheme {
        LumaAppPreviewScaffold {
            FollowingContent(
                followedPeople = samplePeople,
                onPersonClick = {}
            )
        }
    }
}
