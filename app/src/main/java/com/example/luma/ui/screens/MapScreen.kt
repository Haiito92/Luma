package com.example.luma.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.luma.ui.components.LumaNavigationBar
import com.example.luma.ui.theme.LumaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(){
    Scaffold(
        bottomBar = {
            LumaNavigationBar()
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            DockedSearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = "",
                        onQueryChange = {},
                        onSearch = {},
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = { Text("Where to ?") }
                    )
                },
                expanded = false,
                onExpandedChange = {}
            ) { }
        }
    }
}

@Preview
@Composable
fun MapScreenPreview(){
    LumaTheme() {
        MapScreen()
    }
}