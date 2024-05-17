package mkajt.hozana.lekcionar.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mkajt.hozana.lekcionar.ActivityListener
import mkajt.hozana.lekcionar.ui.theme.AppTheme
import mkajt.hozana.lekcionar.util.isInternetAvailable
import mkajt.hozana.lekcionar.util.millisecondsToTimeString
import mkajt.hozana.lekcionar.viewModel.LekcionarViewModel

@Composable
fun MediaPlayer(
    viewModel: LekcionarViewModel,
    uri: String,
    opis: String,
    context: Context,
    snackbarHostState: SnackbarHostState,
    activityListener: ActivityListener?
) {

    val coroutineScope = rememberCoroutineScope()
    val mediaPlayerState by viewModel.mediaPlayerState.collectAsState(Dispatchers.IO)

    if (mediaPlayerState.duration != 0 && opis == mediaPlayerState.title && !mediaPlayerState.isStopped) {
        // Left Column
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .padding(start = 5.dp, end = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    if (mediaPlayerState.isPlaying) {
                        activityListener?.pauseClick()
                    } else {
                        activityListener?.playClick(uri, opis)
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = if (mediaPlayerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = AppTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Right column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Slider(
                        value = mediaPlayerState.currentPosition.toFloat(),
                        onValueChange = { position ->
                            activityListener?.seekClick(position)
                        },
                        valueRange = 0f..mediaPlayerState.duration.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = AppTheme.colorScheme.activeSliderTrack,
                            activeTrackColor = AppTheme.colorScheme.activeSliderTrack,
                            inactiveTrackColor = AppTheme.colorScheme.inactiveSliderTrack
                        )
                    )
                }

                // Second row: current position and duration
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, end = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = millisecondsToTimeString(mediaPlayerState.currentPosition),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.activeSliderTrack,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = millisecondsToTimeString(mediaPlayerState.duration),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.activeSliderTrack,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    } else {
        // Left Column
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .padding(start = 5.dp, end = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    if (!isInternetAvailable(context)) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Ni internetne povezave!", null, true, SnackbarDuration.Short)
                        }
                    } else if (!checkPermission(activityListener?.getActivity())) {
                        activityListener?.requestNotificationPermission()
                    } else {
                        activityListener?.playClick(uri, opis)
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "Play",
                    tint = AppTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Right column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Slider(
                        value = 0f,
                        onValueChange = {},
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = AppTheme.colorScheme.activeSliderTrack,
                            activeTrackColor = AppTheme.colorScheme.activeSliderTrack,
                            inactiveTrackColor = AppTheme.colorScheme.inactiveSliderTrack
                        )
                    )
                }
            }
        }
    }
}

private fun checkPermission(context: Context?): Boolean {
    if (Build.VERSION.SDK_INT >= 33 && context != null) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }
    return true
}
