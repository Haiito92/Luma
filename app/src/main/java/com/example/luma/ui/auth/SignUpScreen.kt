package com.example.luma.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luma.ui.components.LumaTextButton
import com.example.luma.ui.components.LumaTextField
import com.example.luma.ui.theme.LumaTheme

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory),
    onNavigateToLogin: () -> Unit = {}
) {
    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()

    SignUpContent(
        isLoading = authUiState is AuthUiState.Loading,
        errorMessage = (authUiState as? AuthUiState.Error)?.message,
        onSignUpClick = viewModel::onSignUp,
        onNavigateToLogin = {
            onNavigateToLogin()
        }
    )
}

@Composable
fun SignUpContent(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onSignUpClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onNavigateToLogin: () -> Unit = {}
) {
    val firstNameState = rememberTextFieldState()
    val lastNameState = rememberTextFieldState()
    val emailState = rememberTextFieldState()
    val passwordState = rememberTextFieldState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LumaTextField(
            state = firstNameState,
            label = "First Name",
            maxHeightInLines = 1
        )

        LumaTextField(
            state = lastNameState,
            label = "Last Name",
            maxHeightInLines = 1
        )

        LumaTextField(
            state = emailState,
            label = "Email",
            maxHeightInLines = 1
        )

        LumaTextField(
            state = passwordState,
            label = "Password",
            maxHeightInLines = 1
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        LumaTextButton(
            text = if (isLoading) "Loading..." else "Create Account",
            onClick = {
                onSignUpClick(
                    firstNameState.text.toString(),
                    lastNameState.text.toString(),
                    emailState.text.toString(),
                    passwordState.text.toString()
                )
            },
            enabled = !isLoading
        )

        LumaTextButton(
            text = "Back to Login",
            onClick = onNavigateToLogin,
            enabled = !isLoading
        )
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    LumaTheme {
        SignUpContent()
    }
}
