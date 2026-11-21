package com.example.luma.ui.screens

import android.widget.Space
import androidx.activity.compose.BackHandler
import androidx.annotation.experimental.R
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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.data.local.guardian.Guarded
import com.example.luma.data.local.guardian.Guardian
import com.example.luma.data.local.guardian.GuardianRequest
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
    val pendingRequests by viewModel.pendingRequests.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onBackToContacts()
    }

    BackHandler(enabled = state !is GuardiansUiState.ViewingContacts) {
        viewModel.onBackToContacts()
    }

    when (val currentState = state) {
        is GuardiansUiState.ViewingContacts -> {
            GuardiansContent(
                selectedTab = currentState.selectedTab,
                guardians = guardians,
                guardeds = guarded,
                onGuardiansTabClick = { viewModel.onTabSelected(GuardiansTab.Guardians) },
                onGuardedTabClick = { viewModel.onTabSelected(GuardiansTab.Guarded) },
                onAddGuardiansClick = viewModel::onOpenAddGuardian,
                onViewPendingRequestClick = viewModel::onOpenViewPendingRequests,
                onRemoveContact = viewModel::onRemoveGuardian
            )
        }

        is GuardiansUiState.AddingGuardian -> {
            AddGuardiansContent(viewModel::onAddGuardian)
        }

        is GuardiansUiState.ViewingCourse -> {
            FollowingCourseContent(person = currentState.person)
        }

        is GuardiansUiState.ViewingPendingRequests -> {
            PendingRequest(
                pendingRequests,
                viewModel::onAcceptGuardianRequest,
                viewModel::onDeclineGuardianRequest
            )
        }
    }
}

@Composable
fun GuardiansContent(
    selectedTab: GuardiansTab = GuardiansTab.Guardians,
    guardians: List<Guardian> = emptyList(),
    guardeds: List<Guarded> = emptyList(),
    onGuardiansTabClick: () -> Unit = {},
    onGuardedTabClick: () -> Unit = {},
    onAddGuardiansClick: () -> Unit = {},
    onViewPendingRequestClick: () -> Unit = {},
    onRemoveContact: (String) -> Unit = {}
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

        when (selectedTab){
            GuardiansTab.Guardians -> {
                GuardiansList(
                    guardians,
                    onRemoveContact,
                    onAddGuardiansClick
                )
            }
            GuardiansTab.Guarded -> {
                GuardedList(
                    guardeds,
                    onRemoveContact,
                    onViewPendingRequestClick
                )
            }
        }
    }
}

@Composable
fun GuardiansList(
    guardians: List<Guardian>,
    onRemoveContact: (String) -> Unit,
    onAddGuardiansClick: () -> Unit
){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(guardians) { guardian ->
            ContactItem(
                name = guardian.name,
                linkId = guardian.linkId,
                onClick = {  },
                enabled = false,
                onRemoveContact
            )
        }
    }

    LumaTextButton(
        text = "Add Guardians",
        onClick = onAddGuardiansClick
    )
}

@Composable
fun GuardedList(
    guardeds: List<Guarded>,
    onRemoveContact: (String) -> Unit,
    onViewPendingRequestClick: () -> Unit
){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(guardeds) { guarded ->
            ContactItem(
                name = guarded.name,
                linkId = guarded.linkId,
                onClick = {  },
                enabled = false,
                onRemoveContact
            )
        }
    }

    LumaTextButton(
        text = "View Pending Request",
        onClick = onViewPendingRequestClick
    )
}

@Composable
fun ContactItem(
    name: String,
    linkId: String,
    onClick: () -> Unit,
    enabled: Boolean,
    onRemoveContact: (String) -> Unit,
    modifier: Modifier = Modifier,
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
            IconButton({}) {
                IconButton(onClick = { onRemoveContact(linkId) }) { Icon(Icons.Default.Close, contentDescription = "Remove") }
            }
        }
    }
}

@Composable
fun PendingRequest(
    requests: List<GuardianRequest>,
    onAcceptGuardianRequest: (String) -> Unit,
    onDeclineGuardianRequest: (String) -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pending Requests",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(requests) { request ->
                ListItem(
                    headlineContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                            Text(request.walkerName)
                            Text(request.walkerLastName)
                        }
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { onAcceptGuardianRequest(request.linkId)  }){ Icon(Icons.Default.Check, contentDescription = "Accept") }
                            IconButton(onClick = { onDeclineGuardianRequest(request.linkId) }) { Icon(Icons.Default.Close, contentDescription = "Decline") }
                        }
                    }
                )
            }
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
