package com.example.luma.ui.guardians

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
import com.example.luma.ui.app.LumaAppPreviewScaffold
import com.example.luma.ui.components.LumaTextButton
import com.example.luma.ui.components.LumaTextField
import com.example.luma.ui.theme.LumaTheme

@Composable
fun AddGuardiansScreen(
    viewModel: GuardiansViewModel = viewModel(factory = GuardiansViewModelFactory)
) {
    AddGuardiansContent(
        viewModel::onAddGuardian
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
            LumaTextField(state = inputText, label = "Guardian Email", maxHeightInLines = 1)
            LumaTextButton(
                text = "Add",
                onClick = {
                    val guardianEmail = inputText.text.toString()
                    onAddGuardian(guardianEmail)
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
