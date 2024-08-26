package si.kapucini.lekcionar.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import si.kapucini.lekcionar.ui.routes.Screen
import si.kapucini.lekcionar.ui.theme.AppTheme
import si.kapucini.lekcionar.util.timestampToDate
import si.kapucini.lekcionar.viewModel.LekcionarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfo(viewModel: LekcionarViewModel, navController: NavController) {

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = AppTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "O aplikaciji", style = AppTheme.typography.titleLarge)
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
                    .background(color = AppTheme.colorScheme.primary),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AppInfoSection(viewModel = viewModel)
            }
        }
    )
}

@Composable
private fun AppInfoSection(viewModel: LekcionarViewModel) {
    val context = LocalContext.current
    val updateDataTimestamp by viewModel.updatedDataTimestamp.collectAsState()
    val testUpdate by viewModel.testUpdate.collectAsState()
    Column(
        modifier = Modifier
            .padding(top = 40.dp, bottom = 40.dp, start = 30.dp, end = 30.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Zadnja posodobitev podatkov:",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.headerContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                )
                Text(text = timestampToDate(updateDataTimestamp),
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.headerContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 60.dp)
                )
                Text(text = "Test update:",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.headerContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                )
                Text(text = timestampToDate(testUpdate/1000),
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.headerContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 60.dp)
                )

            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Za napake v podatkih in več informacij pišite na:",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.headerContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                )
                Text(text = "info@hozana.si",
                    style = AppTheme.typography.labelLarge.merge(textDecoration = TextDecoration.Underline),
                    color = AppTheme.colorScheme.headerContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 60.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:info@hozana.si")
                            }
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            }
                        }
                )
                Text(text = "Vir podatkov:",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.headerContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                )
                Text(text = "br. Matej Nastran OFMCap",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.headerContent,
                    textAlign = TextAlign.Center
                )
                Text(text = "Zasnova in izdelava aplikacije:",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.headerContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 10.dp)
                )
                Text(text = "Maja Kajtna",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.headerContent,
                    textAlign = TextAlign.Center
                )
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "\u00A9 2024 hozana.si",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.headerContent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                )
                Text(text = "Vse pravice pridržane.",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.headerContent,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}