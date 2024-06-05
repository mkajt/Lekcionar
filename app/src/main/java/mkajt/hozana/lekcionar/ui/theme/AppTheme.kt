package mkajt.hozana.lekcionar.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val lightColorScheme = AppColorScheme(
    primary = LekcionarRed,
    secondary = Black,
    background = White,
    activeSliderTrack = GreyActiveTrack,
    inactiveSliderTrack = GreyInactiveTrack,
    headerContent = White
)

private val darkColorScheme = AppColorScheme(
    primary = LekcionarRed,
    secondary = White,
    background = Grey,
    activeSliderTrack = GreyInactiveTrack,
    inactiveSliderTrack = GreyActiveTrack,
    headerContent = White
)

private val typography = AppTypography(
    titleLarge = TextStyle(
        fontFamily = Roboto_condensed,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp
    ),
    titleNormal = TextStyle(
        fontFamily = Roboto_condensed,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Roboto_condensed,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    body = TextStyle(
        fontFamily = PT_sefif,
        fontSize = 18.sp
    ),
    bodyItalic = TextStyle(
        fontFamily = PT_sefif,
        fontSize = 18.sp,
        fontStyle = FontStyle.Italic
    ),
    labelXLarge = TextStyle(
        fontFamily = PT_sefif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = PT_sefif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    labelNormal = TextStyle(
        fontFamily = PT_sefif,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PT_sefif,
        fontSize = 14.sp
    )
)

private val shape = AppShape(
    container = RoundedCornerShape(12.dp),
    button = RoundedCornerShape(50)
)

private val size = AppSize(
    large = 24.dp,
    medium = 16.dp,
    normal = 12.dp,
    small = 8.dp
)

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
){
    val colorScheme = if (isDarkTheme) darkColorScheme else lightColorScheme
    val rippleIndication = rememberRipple()
    CompositionLocalProvider(
        LocalAppColorSheme provides colorScheme,
        LocalAppTyphography provides typography,
        LocalAppShape provides shape,
        LocalAppSize provides size,
        LocalIndication provides rippleIndication,
        content = content
    )
}

object AppTheme {
    val colorScheme: AppColorScheme
        @Composable get() = LocalAppColorSheme.current

    val typography: AppTypography
        @Composable get() = LocalAppTyphography.current

    val shape: AppShape
        @Composable get() = LocalAppShape.current

    val size: AppSize
        @Composable get() = LocalAppSize.current
}