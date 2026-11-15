package com.example.luma.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LumaTextButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true)
{
    TextButton(onClick = onClick, enabled = enabled, modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),) { Text(text = text) }
}

@Composable
fun LumaToggleItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier)
{
    ListItem(headlineContent = { Text(text = title) }, trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) }, modifier = modifier.clickable { onCheckedChange(!checked) })
}

@Composable
fun LumaTextField(state: TextFieldState, label: String, maxHeightInLines: Int, modifier: Modifier = Modifier)
{
    OutlinedTextField(state = state, label = { Text(label) }, lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = maxHeightInLines), modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
}


@Composable
fun LumaDropdownItem(title: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit, modifier: Modifier = Modifier)
{
    var expanded by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier,
        headlineContent = { Text(text = title) },
        trailingContent = {
            Box {
                TextButton(onClick = { expanded = true })
                {
                    Text(text = selectedOption)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Choose an option")
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false })
                {
                    options.forEach { option -> DropdownMenuItem(text = { Text(text = option) }, onClick = { onOptionSelected(option)
                        expanded = false }) }
                }
            }
        }
    )
}