package com.example.luma.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.ui.components.LumaAppPreviewScaffold
import com.example.luma.ui.components.LumaTextButton
import com.example.luma.ui.components.LumaTextField
import com.example.luma.ui.theme.LumaTheme
import com.example.luma.viewmodel.GuardiansViewModel

@Composable
fun AddGuardiansScreen(
    viewModel: GuardiansViewModel = viewModel(),
    onGuardianAdded: () -> Unit = {}
) {
    AddGuardiansContent(
        onAddGuardian = { guardianId ->
            viewModel.onAddGuardian(guardianId)
            onGuardianAdded()
        }
    )
}

@Composable
fun AddGuardiansContent(onAddGuardian: (String) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add Guardians",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            val inputText = rememberTextFieldState()
            LumaTextField(state = inputText, label = "Guardian ID", maxHeightInLines = 1)
            LumaTextButton(
                text = "Add",
                onClick = {
                    val guardianId = inputText.text.toString()
                    onAddGuardian(guardianId)
                }
            )
        }
    }
}

@Preview
@Composable
fun AddGuardiansScreenPreview() {
    LumaTheme {
        LumaAppPreviewScaffold {
            AddGuardiansContent()
        }
    }
}
