package mkajt.hozana.lekcionar.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mkajt.hozana.lekcionar.ui.routes.Screen
import mkajt.hozana.lekcionar.ui.theme.AppTheme
import mkajt.hozana.lekcionar.util.timestampToDate
import mkajt.hozana.lekcionar.viewModel.LekcionarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(viewModel: LekcionarViewModel, navController: NavController) {

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Nastavitve", style = AppTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.HOME.name)
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = AppTheme.colorScheme.primary,
                    titleContentColor = AppTheme.colorScheme.headerContent,
                    navigationIconContentColor = AppTheme.colorScheme.headerContent
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .fillMaxHeight()
                    .background(color = AppTheme.colorScheme.background)
            ) {
                SettingsSection(viewModel = viewModel)
            }
        }
    )
}

@Composable
private fun SettingsSection(viewModel: LekcionarViewModel) {
    Column(
        modifier = Modifier
            .padding(top = 60.dp, bottom = 8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DropDownMenuSkofija(viewModel)
        DropDownMenuRed(viewModel)
        SwitchTheme(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropDownMenuSkofija(viewModel: LekcionarViewModel) {
    val selectedSkofija by viewModel.selectedSkofija.collectAsState()
    val skofijaList by viewModel.skofijaList.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedSkofija) }

    Column(modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
        ) {
            Text(text = "Izberi škofijo:",
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colorScheme.secondary,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
            )
        }

        Box(modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Row(modifier = Modifier
                    .align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp)
                        .background(color = AppTheme.colorScheme.background)
                ) {
                    OutlinedTextField(
                        textStyle = AppTheme.typography.body.merge(textAlign = TextAlign.Center, color = AppTheme.colorScheme.secondary),
                        shape = AppTheme.shape.button,
                        value = skofijaList?.find { item -> item.id == selectedText }?.skofija ?: "Slovenski splošni koledar",
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = {
                            val icon = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown
                            Icon(icon, "arrow", modifier = Modifier.size(36.dp))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .border(
                                border = BorderStroke(2.dp, AppTheme.colorScheme.inactiveSliderTrack),
                                shape = AppTheme.shape.button
                            ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppTheme.colorScheme.inactiveSliderTrack,
                            unfocusedBorderColor = AppTheme.colorScheme.inactiveSliderTrack,
                            focusedContainerColor = AppTheme.colorScheme.background,
                            unfocusedContainerColor = AppTheme.colorScheme.background,
                            focusedTrailingIconColor = AppTheme.colorScheme.primary,
                            unfocusedTrailingIconColor = AppTheme.colorScheme.primary,
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        scrollState = rememberScrollState(),
                        modifier = Modifier
                            .background(AppTheme.colorScheme.background)
                            .requiredSizeIn(maxHeight = 250.dp)
                    ) {
                        skofijaList?.forEach{ item ->
                            DropdownMenuItem(
                                text = { Text(text = item.skofija, style = AppTheme.typography.labelNormal, color = AppTheme.colorScheme.secondary) },
                                onClick = {
                                    selectedText = item.id
                                    expanded = false
                                    viewModel.setSelectedSkofija(selectedText)
                                },
                                modifier = Modifier
                                    .background(AppTheme.colorScheme.background),
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropDownMenuRed(viewModel: LekcionarViewModel) {
    val selectedRed by viewModel.selectedRed.collectAsState()
    val redList by viewModel.redList.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedRed) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
        ) {
            Text(text = "Izberi red:",
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colorScheme.secondary,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
            )
        }

        Box(modifier = Modifier
            .align(Alignment.CenterHorizontally)
        ) {
            Row(modifier = Modifier
                .align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp)
                        .background(color = AppTheme.colorScheme.background)
                ) {
                    OutlinedTextField(
                        textStyle = AppTheme.typography.body.merge(textAlign = TextAlign.Center, color = AppTheme.colorScheme.secondary),
                        shape = AppTheme.shape.button,
                        value = redList?.find { item -> item.id == selectedText }?.red ?: "Brez izbire reda",
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = {
                            val icon = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown
                            Icon(icon, "arrow", modifier = Modifier.size(36.dp))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .border(
                                border = BorderStroke(2.dp, AppTheme.colorScheme.inactiveSliderTrack),
                                shape = AppTheme.shape.button
                            ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppTheme.colorScheme.inactiveSliderTrack,
                            unfocusedBorderColor = AppTheme.colorScheme.inactiveSliderTrack,
                            focusedContainerColor = AppTheme.colorScheme.background,
                            unfocusedContainerColor = AppTheme.colorScheme.background,
                            focusedTrailingIconColor = AppTheme.colorScheme.primary,
                            unfocusedTrailingIconColor = AppTheme.colorScheme.primary,
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        scrollState = rememberScrollState(),
                        modifier = Modifier
                            .background(AppTheme.colorScheme.background)
                            .requiredSizeIn(maxHeight = 250.dp)
                    ) {
                        redList?.forEach{ item ->
                            DropdownMenuItem(
                                text = { Text(text = item.red, style = AppTheme.typography.labelNormal, color = AppTheme.colorScheme.secondary) },
                                onClick = {
                                    selectedText = item.id
                                    expanded = false
                                    viewModel.setSelectedRed(selectedText)
                                },
                                modifier = Modifier
                                    .background(AppTheme.colorScheme.background),
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitchTheme(viewModel: LekcionarViewModel) {
    val selectedTheme by viewModel.isDarkTheme.collectAsState()
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 60.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
        ) {
            Text(text = "Izberi temo:",
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colorScheme.secondary,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
            )
        }
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Switch(modifier = Modifier
                    .scale(1.5f),
                checked = selectedTheme,
                onCheckedChange = {
                    viewModel.setIsDarkTheme(!selectedTheme)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AppTheme.colorScheme.background,
                    checkedBorderColor = AppTheme.colorScheme.inactiveSliderTrack,
                    checkedTrackColor = AppTheme.colorScheme.primary,
                    uncheckedThumbColor = AppTheme.colorScheme.background,
                    uncheckedBorderColor = AppTheme.colorScheme.inactiveSliderTrack,
                    uncheckedTrackColor = AppTheme.colorScheme.primary
                )
            )
        }
    }
}
