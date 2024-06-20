package si.hozana.lekcionar.mediaPlayer

import android.net.Uri

data class MediaPlayerState (
    var isPlaying: Boolean = false,
    var isStopped: Boolean = true,
    var currentPosition: Int = 0,
    var duration: Int = 0,
    var title: String = "",
    var uri: Uri? = null
)
