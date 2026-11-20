package com.example.luma.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.ui.components.LumaAppPreviewScaffold
import com.example.luma.ui.components.LumaTextButton
import com.example.luma.ui.theme.LumaTheme
import com.example.luma.viewmodel.GuardiansTab
import com.example.luma.viewmodel.GuardiansUiState
import com.example.luma.viewmodel.GuardiansViewModel
import com.example.luma.viewmodel.GuardiansViewModelFactory

@Composable
fun GuardiansScreen(
    viewModel: GuardiansViewModel = viewModel(factory = GuardiansViewModelFactory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val guardians by viewModel.guardians.collectAsStateWithLifecycle()
    val guarded by viewModel.guarded.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onBackToContacts()
    }

    BackHandler(enabled = state !is GuardiansUiState.ViewingContacts) {
        viewModel.onBackToContacts()
    }

    when (val currentState = state) {
        is GuardiansUiState.ViewingContacts -> {
            val contacts = when (currentState.selectedTab) {
                GuardiansTab.Guardians -> guardians
                GuardiansTab.Guarded -> guarded
            }

            GuardiansContent(
                selectedTab = currentState.selectedTab,
                contacts = contacts,
                onGuardiansTabClick = { viewModel.onTabSelected(GuardiansTab.Guardians) },
                onGuardedTabClick = { viewModel.onTabSelected(GuardiansTab.Guarded) },
                onContactClick = { name ->
                    if (currentState.selectedTab == GuardiansTab.Guarded) {
                        viewModel.onGuardedContactClicked(name)
                    }
                },
                onAddGuardiansClick = viewModel::onOpenAddGuardian
            )
        }

        is GuardiansUiState.AddingGuardian -> {
            AddGuardiansContent(viewModel::onAddGuardian)
        }

        is GuardiansUiState.ViewingCourse -> {
            FollowingCourseContent(person = currentState.person)
        }
    }
}

@Composable
fun GuardiansContent(
    selectedTab: GuardiansTab = GuardiansTab.Guardians,
    contacts: List<String> = listOf("Jean-Baptiste Michelin", "Pierre Chabatte"),
    onGuardiansTabClick: () -> Unit = {},
    onGuardedTabClick: () -> Unit = {},
    onContactClick: (String) -> Unit = {},
    onAddGuardiansClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LumaTextButton(
                text = "Guardians",
                onClick = onGuardiansTabClick,
                modifier = Modifier.weight(1f)
            )
            LumaTextButton(
                text = "Guarded",
                onClick = onGuardedTabClick,
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(contacts) { name ->
                ContactItem(
                    name = name,
                    onClick = { onContactClick(name) },
                    enabled = selectedTab == GuardiansTab.Guarded
                )
            }
        }

        if (selectedTab == GuardiansTab.Guardians) {
            LumaTextButton(
                text = "Add Guardians",
                onClick = onAddGuardiansClick
            )
        }
    }
}

@Composable
fun ContactItem(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview
@Composable
fun GuardiansScreenPreview() {
    LumaTheme {
        LumaAppPreviewScaffold {
            GuardiansContent()
        }
    }
}
