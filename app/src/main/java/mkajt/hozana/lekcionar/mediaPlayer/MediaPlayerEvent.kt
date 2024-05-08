package mkajt.hozana.lekcionar.mediaPlayer

import android.content.Context
import android.net.Uri

sealed class MediaPlayerEvent {

    data class Initialize(
        val uri : Uri,
        val context : Context
    ) : MediaPlayerEvent()

    object Play : MediaPlayerEvent()

    object Pause : MediaPlayerEvent()

    object Stop : MediaPlayerEvent()

    data class Seek(val position: Float) : MediaPlayerEvent()
}