package si.hozana.lekcionar.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

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

@SuppressLint("SimpleDateFormat")
fun timestampToDate(timestamp: Long): String {
    if (timestamp == 0L) {
        return "Ni podatka"
    }

    return try {
        val dateFormat = SimpleDateFormat("d. M. yyyy H:mm")
        val dateTime = Date(timestamp * 1000)
        dateFormat.format(dateTime)
    } catch (e: Exception) {
        "Neznan datum"
    }
}