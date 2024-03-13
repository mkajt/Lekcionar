package mkajt.hozana.lekcionar.ui.components

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import mkajt.hozana.lekcionar.util.millisecondsToTimeString
import mkajt.hozana.lekcionar.model.database.PodatkiEntity
import mkajt.hozana.lekcionar.ui.theme.LekcionarRed
import mkajt.hozana.lekcionar.ui.theme.White
import mkajt.hozana.lekcionar.viewModel.LekcionarViewModel
import mkajt.hozana.lekcionar.viewModel.LekcionarViewState
import mkajt.hozana.lekcionar.viewModel.MediaPlayerEvent
import java.text.SimpleDateFormat

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
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        Toast.makeText(context, "Bottom", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowLeft,
                            contentDescription = "Previous Date",
                            modifier = Modifier.size(450.dp)
                        )
                    }
                    IconButton(onClick = {
                        Toast.makeText(context, "Bottom", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.DateRange,
                            contentDescription = "Callendar",
                            modifier = Modifier
                                .size(450.dp)
                                .padding(horizontal = 5.dp)
                        )
                    }
                    IconButton(onClick = {
                        Toast.makeText(context, "Bottom", Toast.LENGTH_SHORT).show()
                    }) {
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
    val podatki by viewModel.podatki.collectAsState()

    val errorMessage = when (dataState) {
        is LekcionarViewState.Error -> (dataState as LekcionarViewState.Error).errorMessage
        else -> null
    }
    if (errorMessage == null) {
        if (dataState.equals(LekcionarViewState.Loading)) {
            Greeting(name = "Loading", modifier = Modifier.padding(innerPadding))
            Log.d("UI", "Loading")
        } else if (dataState.equals(LekcionarViewState.AlreadyInDb) || dataState.equals(
                LekcionarViewState.Loaded
            )
        ) {
            //Greeting(name = "AlreadyInDb", modifier = Modifier.padding(innerPadding))
            podatki?.let {
                for (podatek in podatki!!) {
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DisplayData(podatki = podatek, viewModel = viewModel)
                    }
                }
            }
            Log.d("UI", "AlreadyInDb or Loaded")
        } /*else if (!idPodatek.isNullOrEmpty()) {
            Greeting(name = idPodatek!![0], modifier = Modifier.padding(innerPadding))
        } */
        else if (dataState.equals(LekcionarViewState.Start)) {
            Greeting(name = "Start", modifier = Modifier.padding(innerPadding))
            Log.d("UI", "Start")
        } /*else if (dataState.equals(LekcionarViewState.Loaded)){
            Greeting(name = "Loaded", modifier = Modifier.padding(innerPadding))
            Log.d("UI", "Loaded")
        }*/
    } else {
        Text(
            text = errorMessage,
            modifier = Modifier.padding(innerPadding),
            color = LekcionarRed
        )
    }

}

@Preview(showBackground = true)
@Composable
fun DisplayData(podatki: PodatkiEntity, viewModel: LekcionarViewModel) {
    var isButtonClicked by remember { mutableStateOf(false) }
    val context = LocalContext.current.applicationContext

    val mediaPlayerState by viewModel.mediaPlayerState.collectAsState(Dispatchers.IO)
    //val mediaPlayerState = viewModel.mediaPlayerState
    Text(text = podatki.datum, Modifier.padding(top = 10.dp))

    OutlinedButton(
        onClick = {
            isButtonClicked = !isButtonClicked
            Log.d("DisplayData", isButtonClicked.toString())
        },
        modifier = Modifier.padding(top = 10.dp, end = 10.dp)
    ) {
        Text(text = podatki.opis)
    }
    /*(Row(horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()){
        Box(modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center){
            Text(text = podatki.vrstica, textAlign = TextAlign.Center)
        }
    }*/
    /*Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(5f))
        Text(
            text = podatki.vrstica,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(IntrinsicSize.Max)
        )
        Spacer(modifier = Modifier.weight(5f))
    }*/
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = podatki.vrstica,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(0.75f)
        )
    }

    if (podatki.mp3.isNotEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TimeBar(
                modifier = Modifier.fillMaxWidth(),
                viewModel = viewModel,
                uri = podatki.mp3,
                context = context
            )
        }
    }

}

@Composable
private fun TimeBar(
    modifier: Modifier,
    viewModel: LekcionarViewModel,
    uri: String,
    context: Context
) {

    val mediaPlayerState by viewModel.mediaPlayerState.collectAsState(Dispatchers.IO)
    /*val mediaPlayerState = remember { mutableStateOf(viewModel.mediaPlayerState.value) }

    LaunchedEffect(Unit) {
        viewModel.mediaPlayerState.collect { newState ->
            mediaPlayerState.value = newState
        }
    }*/
    Log.d("TimeBar", mediaPlayerState.currentPosition.toString())

    Box(modifier = modifier) {
        if (mediaPlayerState.duration != 0) {
            Row(
                modifier = modifier
                    .align(Alignment.TopCenter)
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Slider(
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
                    value = mediaPlayerState.currentPosition.toFloat(),
                    onValueChange = { position ->
                        viewModel.onMediaPlayerEvent(
                            event = MediaPlayerEvent.Seek(
                                position
                            )
                        )
                    },
                    valueRange = 0f..mediaPlayerState.duration.toFloat()
                )
                IconButton(onClick = {
                    if (mediaPlayerState.isPlaying) {
                        viewModel.onMediaPlayerEvent(event = MediaPlayerEvent.Pause)
                    } else {
                        viewModel.onMediaPlayerEvent(
                            event = MediaPlayerEvent.Initialize(
                                Uri.parse(uri),
                                context
                            )
                        )
                    }
                }) {
                    Icon(
                        imageVector = if (mediaPlayerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = LekcionarRed,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(end = 20.dp)
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center){
                Text(
                    text = millisecondsToTimeString(mediaPlayerState.currentPosition),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LekcionarRed,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )

                Text(
                    text = millisecondsToTimeString(mediaPlayerState.duration),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LekcionarRed,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        } else {
            Row(modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Slider(
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
                    value = mediaPlayerState.currentPosition.toFloat(),
                    onValueChange = {},
                    valueRange = 0f..1f
                )
                IconButton(
                    onClick = {
                        viewModel.onMediaPlayerEvent(
                            event = MediaPlayerEvent.Initialize(
                                Uri.parse(uri),
                                context
                            )
                        )
                    }){
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Play",
                        tint = LekcionarRed,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }

}

private fun createSelektor(date: String, viewModel: LekcionarViewModel) {
    val selektor = "$date-slovenija-kapucini"
    viewModel.updateSelektor(selektor)
}