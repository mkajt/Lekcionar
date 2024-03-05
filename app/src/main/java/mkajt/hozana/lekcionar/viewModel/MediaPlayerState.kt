package mkajt.hozana.lekcionar.viewModel

data class MediaPlayerState (
    var isPlaying: Boolean = false,
    var currentPosition: Int = 0,
    var duration: Int = 0
)
