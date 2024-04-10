package mkajt.hozana.lekcionar.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

data class AppColorScheme(
    val background: Color,
    val primary: Color,
    val secondary: Color,
    val activeSliderTrack: Color,
    val inactiveSliderTrack: Color,
)

data class AppTypography(
    val titleLarge: TextStyle,
    val titleNormal: TextStyle,
    val titleSmall: TextStyle,
    val body: TextStyle,
    val bodyItalic: TextStyle,
    val labelXLarge: TextStyle,
    val labelLarge: TextStyle,
    val labelNormal: TextStyle,
    val labelSmall: TextStyle
)

data class AppShape(
    val container: Shape,
    val button: Shape
)

data class AppSize(
    val large: Dp,
    val medium: Dp,
    val normal: Dp,
    val small: Dp
)

val LocalAppColorSheme = staticCompositionLocalOf {
    AppColorScheme(
        background = Color.Unspecified,
        primary = Color.Unspecified,
        secondary = Color.Unspecified,
        activeSliderTrack = Color.Unspecified,
        inactiveSliderTrack = Color.Unspecified
    )
}

val LocalAppTyphography = staticCompositionLocalOf {
    AppTypography(
        titleLarge = TextStyle.Default,
        titleNormal = TextStyle.Default,
        titleSmall = TextStyle.Default,
        body = TextStyle.Default,
        bodyItalic = TextStyle.Default,
        labelXLarge = TextStyle.Default,
        labelLarge = TextStyle.Default,
        labelNormal = TextStyle.Default,
        labelSmall = TextStyle.Default
    )
}

val LocalAppShape = staticCompositionLocalOf {
    AppShape(
        container = RectangleShape,
        button = RectangleShape
    )
}

val LocalAppSize = staticCompositionLocalOf {
    AppSize(
        large = Dp.Unspecified,
        medium = Dp.Unspecified,
        normal = Dp.Unspecified,
        small = Dp.Unspecified
    )
}