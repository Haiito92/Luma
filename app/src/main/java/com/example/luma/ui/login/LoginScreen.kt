package com.example.luma.ui.login

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
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory),
    onLoginSuccess: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is AuthUiState.Authenticated) {
            viewModel.resetState()
            onLoginSuccess()
        }
    }

    LoginContent(
        isLoading = state is AuthUiState.Loading,
        errorMessage = (state as? AuthUiState.Error)?.message,
        onLoginClick = viewModel::onLogin,
        onNavigateToSignUp = {
            viewModel.resetState()
            onNavigateToSignUp()
        }
    )
}

@Composable
fun LoginContent(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onNavigateToSignUp: () -> Unit = {}
) {
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
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
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
            text = if (isLoading) "Loading..." else "Log In",
            onClick = {
                onLoginClick(
                    emailState.text.toString(),
                    passwordState.text.toString()
                )
            },
            enabled = !isLoading
        )

        LumaTextButton(
            text = "Sign Up",
            onClick = onNavigateToSignUp,
            enabled = !isLoading
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LumaTheme {
        LoginContent()
    }
}
