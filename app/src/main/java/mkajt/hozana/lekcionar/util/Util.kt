package mkajt.hozana.lekcionar.util

fun millisecondsToTimeString(milliseconds: Int): String {
    var result = ""
    var secondsString = ""
    val hours = milliseconds / (1000 * 60 * 60)
    val minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000 )
    if (hours > 0) {
        result = "$hours:"
    }
    secondsString = if (seconds < 10) {
        "0$seconds"
    } else {
        "$seconds"
    }
    result = "$result$minutes:$secondsString"
    return result
}
