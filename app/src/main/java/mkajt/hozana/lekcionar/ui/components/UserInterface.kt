package mkajt.hozana.lekcionar.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import mkajt.hozana.lekcionar.ui.theme.LekcionarRed
import mkajt.hozana.lekcionar.ui.theme.White
import mkajt.hozana.lekcionar.viewModel.LekcionarViewModel
import mkajt.hozana.lekcionar.viewModel.LekcionarViewState
import java.text.SimpleDateFormat
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun UserInterface(viewModel: LekcionarViewModel) {
    val context = LocalContext.current.applicationContext

    val formatter = SimpleDateFormat("yyyy-MM-dd")
    val formattedDate = formatter.format(System.currentTimeMillis())
    createSelektor(formattedDate.toString(), viewModel)

    viewModel.getPodatkiBySelektor()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Lekcionar")
                },
                actions = {

                    // Settings
                    IconButton(onClick = {
                        Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = White,
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    // App info
                    IconButton(onClick = {
                        Toast.makeText(context, "App info", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "App info",
                            tint = White,
                            modifier = Modifier.size(25.dp)
                        )
                    }

                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = LekcionarRed
                )
            )
        },
        content = { innerPadding ->
            ContentSectionUI(innerPadding = innerPadding, viewModel = viewModel)
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

            }
            BottomAppBar(
                containerColor = White,
                contentColor = LekcionarRed,
                contentPadding = BottomAppBarDefaults.ContentPadding,
                modifier = Modifier
                    .height(80.dp)
                    /*.clip(
                        RoundedCornerShape(
                            topStart = 24.dp, topEnd = 24.dp
                        )
                    )*/
            ){
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { Toast.makeText(context, "Bottom", Toast.LENGTH_SHORT).show() }) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowLeft,
                            contentDescription = "Previous Date",
                            modifier = Modifier.size(450.dp)
                        )
                    }
                    IconButton(onClick = { Toast.makeText(context, "Bottom", Toast.LENGTH_SHORT).show() }) {
                        Icon(
                            imageVector = Icons.Rounded.DateRange,
                            contentDescription = "Callendar",
                            modifier = Modifier
                                .size(450.dp)
                                .padding(horizontal = 5.dp)
                        )
                    }
                    IconButton(onClick = { Toast.makeText(context, "Bottom", Toast.LENGTH_SHORT).show() }) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowRight,
                            contentDescription = "Next Date",
                            modifier = Modifier.size(450.dp)
                        )
                    }
                }
            }
        }


    )
}

@Preview(showBackground = true)
@Composable
fun ContentSectionUI(innerPadding: PaddingValues, viewModel: LekcionarViewModel) {

    val dataState by viewModel.dataState.collectAsState()
    val idPodatek by viewModel.idPodatek.collectAsState()

    if (dataState.equals(LekcionarViewState.Loading)) {
        //Greeting(name = "Loading", modifier = Modifier.padding(innerPadding))
        Greeting(name = idPodatek, modifier = Modifier.padding(innerPadding))
        Log.d("UI", "Loading")
    } else if (dataState.equals(LekcionarViewState.Start)){
        //Greeting(name = "Start", modifier = Modifier.padding(innerPadding))
        Greeting(name = idPodatek, modifier = Modifier.padding(innerPadding))
        Log.d("UI", "Start")
    } else {
        //Greeting(name = "Loaded", modifier = Modifier.padding(innerPadding))
        Greeting(name = idPodatek, modifier = Modifier.padding(innerPadding))
        Log.d("UI", "Loaded")
    }
}

private fun createSelektor(date: String, viewModel: LekcionarViewModel) {
    val selektor = "$date-slovenija-kapucini"
    viewModel.updateSelektor(selektor)
}