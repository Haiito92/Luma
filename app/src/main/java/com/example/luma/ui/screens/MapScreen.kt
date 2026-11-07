package com.example.luma.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.times
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

            var query by rememberSaveable { mutableStateOf("") }
            var expanded by rememberSaveable { mutableStateOf(false) }

            // List just for test
            // TODO remove the list from here and put it in the view model
            val places = listOf("Home", "Restaurant", "Gym", "Gaz Station", "Zoo", "Shop", "Pharmacy", "Apothecary")
            val matches = places.filter { it.contains(query, ignoreCase = true) }.sorted()

            // Input field is 56.dp by default
            val inputFieldHeight: Dp = SearchBarDefaults.InputFieldHeight;
            val heightInMax = 6 * inputFieldHeight

            var heightIn: Dp = 0.dp
            if (expanded){
                val matchesHeightIn: Dp = inputFieldHeight + matches.size * inputFieldHeight
                val noMatchHeightIn: Dp = 2 * inputFieldHeight
                heightIn = if (matches.isEmpty()) min(noMatchHeightIn, heightInMax) else min(matchesHeightIn, heightInMax)
            }
            else heightIn = inputFieldHeight

            DockedSearchBar(modifier = Modifier.heightIn(max = heightIn),
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.height(inputFieldHeight),
                        query = query,
                        onQueryChange = { query = it},
                        onSearch = {},
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = { Text("Where to ?") }
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                LazyColumn() {
                    if(matches.isEmpty()){
                        items(listOf("No match...")){ string ->
                            ListItem(
                                headlineContent = { Text(string) },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            )
                        }
                    }
                    else{
                        items(matches){ place ->
                            ListItem(
                                headlineContent = { Text(place) },
                                leadingContent = { Icon(Icons.Default.Place, contentDescription = place) },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                modifier = Modifier.clickable{
                                    query = place
                                    expanded = false
                                    // TODO Call view model function for onDestinationSelected
                                }
                            )
                        }
                    }

                }
            }
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