package si.hozana.lekcionar.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Typeface
import androidx.core.content.res.ResourcesCompat
import si.hozana.lekcionar.R

fun getRobotoCondensedFont(context: Context): FontFamily {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FontFamily(Font(R.font.roboto_condensed, FontWeight.Normal))
    } else {
        val typeface = ResourcesCompat.getFont(context, R.font.roboto_condensed_not_variable)
        if (typeface != null) {
            FontFamily(Typeface(typeface))
        } else {
            FontFamily.Monospace
        }
    }
}

fun getPTSerifFont(context: Context): FontFamily {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FontFamily(Font(R.font.pt_serif, FontWeight.Normal))
    } else {
        val typeface = ResourcesCompat.getFont(context, R.font.pt_serif)
        if (typeface != null) {
            FontFamily(Typeface(typeface))
        } else {
            FontFamily.Serif
        }
    }
}