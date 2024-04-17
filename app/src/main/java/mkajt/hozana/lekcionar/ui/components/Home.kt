package mkajt.hozana.lekcionar.ui.components

import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import mkajt.hozana.lekcionar.model.database.PodatkiEntity
import mkajt.hozana.lekcionar.ui.routes.Screen
import mkajt.hozana.lekcionar.ui.theme.AppTheme
import mkajt.hozana.lekcionar.ui.theme.LekcionarRed
import mkajt.hozana.lekcionar.ui.theme.White
import mkajt.hozana.lekcionar.viewModel.LekcionarViewModel
import mkajt.hozana.lekcionar.viewModel.LekcionarViewState
import java.util.regex.Matcher
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(viewModel: LekcionarViewModel, navController: NavController) {
    val context = LocalContext.current.applicationContext

    createSelektor(viewModel)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Lekcionar", style = AppTheme.typography.titleLarge)
                },
                actions = {

                    // Settings
                    IconButton(onClick = {
                        Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
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
                            //tint = White,
                            modifier = Modifier.size(25.dp)
                        )
                    }

                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = AppTheme.colorScheme.primary,
                    actionIconContentColor = AppTheme.colorScheme.background,
                    titleContentColor = AppTheme.colorScheme.background
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                ContentSectionHome(innerPadding = innerPadding, viewModel = viewModel)
            }
        },
        bottomBar = {
            /*Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

            }*/
            BottomAppBar(
                containerColor = White,
                contentColor = LekcionarRed,
                contentPadding = BottomAppBarDefaults.ContentPadding,
                //modifier = Modifier
                 //   .height(80.dp)
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
                        viewModel.goToPreviousDate()
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowLeft,
                            contentDescription = "Previous Date",
                            modifier = Modifier.size(450.dp)
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.CALLENDAR.name)
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
                        viewModel.goToNextDate()
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

@Composable
fun ContentSectionHome(innerPadding: PaddingValues, viewModel: LekcionarViewModel) {

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
            if (podatki != null) {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = podatki!!.first().datum, Modifier.padding(top = 20.dp),
                        style = AppTheme.typography.titleNormal
                    )
                    if (podatki!!.size == 1) {
                        DisplayDataSingle(podatki = podatki!!.first(), viewModel = viewModel)
                    } else {
                        for (podatek in podatki!!) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                DisplayDataMultiple(podatki = podatek, viewModel = viewModel)
                            }
                        }
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

@Composable
private fun DisplayDataSingle(podatki: PodatkiEntity, viewModel: LekcionarViewModel) {
    val context = LocalContext.current.applicationContext

    OutlinedButton(
        onClick = {},
        modifier = Modifier.padding(top = 10.dp, end = 10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AppTheme.colorScheme.background
        ),
        enabled = false,
        shape = AppTheme.shape.button,
        border = BorderStroke(1.5.dp, AppTheme.colorScheme.activeSliderTrack)
    ) {
        Text(text = podatki.opis, style = AppTheme.typography.labelLarge, color = AppTheme.colorScheme.primary)
    }

    if (podatki.vrstica.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = podatki.vrstica,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(0.75f),
                style = AppTheme.typography.bodyItalic
            )
        }
    }

    if (podatki.mp3.isNotEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            MediaPlayer(
                viewModel = viewModel,
                uri = podatki.mp3,
                context = context
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Berila(podatki = podatki)
    }

}

@Composable
private fun DisplayDataMultiple(podatki: PodatkiEntity, viewModel: LekcionarViewModel) {
    var isButtonClicked by remember { mutableStateOf(false) }
    val context = LocalContext.current.applicationContext

    OutlinedButton(
        onClick = {
            isButtonClicked = !isButtonClicked
        },
        modifier = Modifier
            .padding(top = 10.dp, end = 10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isButtonClicked) AppTheme.colorScheme.background else AppTheme.colorScheme.primary,
                contentColor = if (isButtonClicked) AppTheme.colorScheme.primary else AppTheme.colorScheme.background,
            ),
        shape = AppTheme.shape.button,
        border = if (isButtonClicked) BorderStroke(1.5.dp, AppTheme.colorScheme.activeSliderTrack) else BorderStroke(1.5.dp, AppTheme.colorScheme.primary)
    ) {
        Text(text = podatki.opis, style = AppTheme.typography.labelLarge)
    }

    if (isButtonClicked) {

        if (podatki.vrstica.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = podatki.vrstica,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(0.75f),
                    style = AppTheme.typography.bodyItalic
                )
            }
        }

        if (podatki.mp3.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                MediaPlayer(
                    viewModel = viewModel,
                    uri = podatki.mp3,
                    context = context
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Berila(podatki = podatki)
        }
    }
}

@Composable
private fun Berila(podatki: PodatkiEntity) {
    Column{
        if (podatki.berilo1 != "") {
            Berilo(podatki = podatki, type = "1. berilo")
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(16.dp))
        }
        if (podatki.psalm != "") {
            Psalm(podatki = podatki)
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(16.dp))
        }

        if (podatki.berilo2 != "") {
            Berilo(podatki = podatki, type = "2. berilo")
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(16.dp))
        }
        if (podatki.evangelij != "") {
            Evangelij(podatki = podatki)
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(16.dp))
        }
    }
}

@Composable
private fun Berilo(podatki: PodatkiEntity, type: String) {
    var open by remember { mutableStateOf(false) }
    val borderColor = AppTheme.colorScheme.inactiveSliderTrack

    LaunchedEffect(podatki) {
        open = false
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(
                onClick = {
                    open = !open
                }
            )
            .drawBehind {
                val borderSize = 1.dp

                val strokeWidth = borderSize.value * density
                val y2 = size.height - strokeWidth / 2

                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx()
                )

                drawLine(
                    color = borderColor,
                    start = Offset(0f, y2),
                    end = Offset(size.width, y2),
                    strokeWidth = 2.dp.toPx()
                )

            },
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start

        ) {
            Text(
                text = "$type: ",
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colorScheme.primary,
                textAlign = TextAlign.Start
            )
            if (type == "1. berilo") {
                Text(
                    text = podatki.berilo1,
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.activeSliderTrack,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            } else {
                Text(
                    text = podatki.berilo2,
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.activeSliderTrack,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }

            IconButton(
                onClick = {
                    open = !open
                }
            ) {
                Icon(
                    imageVector = if (open) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Extend",
                    modifier = Modifier
                        .weight(1f)
                        .size(36.dp),
                    tint = if (open) AppTheme.colorScheme.inactiveSliderTrack else AppTheme.colorScheme.primary
                )
            }
        }
        if (open) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                if (type == "1. berilo") {
                    HtmlBody(text = podatki.berilo1_vsebina)
                } else {
                    HtmlBody(text = podatki.berilo2_vsebina)
                }
            }
        }
    }
}

@Composable
private fun Psalm(podatki: PodatkiEntity) {
    var open by remember { mutableStateOf(false) }
    val borderColor = AppTheme.colorScheme.inactiveSliderTrack

    LaunchedEffect(podatki) {
        open = false
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(
                onClick = {
                    open = !open
                }
            )
            .drawBehind {
                val borderSize = 1.dp

                val strokeWidth = borderSize.value * density
                val y2 = size.height - strokeWidth / 2

                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx()
                )

                drawLine(
                    color = borderColor,
                    start = Offset(0f, y2),
                    end = Offset(size.width, y2),
                    strokeWidth = 2.dp.toPx()
                )

            },
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text =  "Psalm: ",
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colorScheme.primary,
                textAlign = TextAlign.Start
            )

            Text(
                text =  podatki.psalm,
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colorScheme.activeSliderTrack,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )

            IconButton(
                onClick = {
                    open = !open
                }
            ) {
                Icon(
                    imageVector = if (open) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Extend",
                    modifier = Modifier
                        .weight(1f)
                        .size(36.dp),
                    tint = if (open) AppTheme.colorScheme.inactiveSliderTrack else AppTheme.colorScheme.primary
                )
            }
        }
        if (open) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Start
                ){

                    Text(
                        text = "Odpev: ",
                        style = AppTheme.typography.body,
                        color = AppTheme.colorScheme.primary
                    )
                    Text(
                        text = podatki.odpev,
                        style = AppTheme.typography.bodyItalic,
                        color = AppTheme.colorScheme.secondary
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HtmlBody(text = addItalicStyleToPsalm(podatki.psalm_vsebina))
                }
            }
        }
    }
}

@Composable
private fun Evangelij(podatki: PodatkiEntity) {
    var open by remember { mutableStateOf(false) }
    val borderColor = AppTheme.colorScheme.inactiveSliderTrack

    LaunchedEffect(podatki) {
        open = false
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(
                onClick = {
                    open = !open
                }
            )
            .drawBehind {
                val borderSize = 1.dp

                val strokeWidth = borderSize.value * density
                val y2 = size.height - strokeWidth / 2

                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx()
                )

                drawLine(
                    color = borderColor,
                    start = Offset(0f, y2),
                    end = Offset(size.width, y2),
                    strokeWidth = 2.dp.toPx()
                )

            },
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text =  "Evangelij: ",
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colorScheme.primary,
                textAlign = TextAlign.Start
            )
            Text(
                text =  podatki.evangelij,
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colorScheme.activeSliderTrack,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )

            IconButton(
                onClick = {
                    open = !open
                }
            ) {
                Icon(
                    imageVector = if (open) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Extend",
                    modifier = Modifier
                        .weight(1f)
                        .size(36.dp),
                    tint = if (open) AppTheme.colorScheme.inactiveSliderTrack else AppTheme.colorScheme.primary
                )
            }
        }
        if (open) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(start = 16.dp, end = 16.dp)
            ) {

                if (podatki.aleluja != "") {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start
                    ){
                        Text(
                            text = "Aleluja: ",
                            style = AppTheme.typography.body,
                            color = AppTheme.colorScheme.primary
                        )
                        HtmlBodyItalic(text = addBreakAfterSourceOfAleluja(podatki.aleluja))
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HtmlBody(text = podatki.evangelij_vsebina)
                    }
                } else {
                    HtmlBody(text = podatki.evangelij_vsebina)
                }

            }
        }
    }
}

@Composable
private fun HtmlBody(text: String) {
    val textSizeBody = AppTheme.typography.body.fontSize.value
    val textColorBody = AppTheme.colorScheme.secondary
    val textFontBody = AppTheme.typography.body

    val resolver: FontFamily.Resolver = LocalFontFamilyResolver.current
    val typefaceBody: android.graphics.Typeface = remember(resolver, textFontBody) {
        resolver.resolve(
            fontFamily = textFontBody.fontFamily
        )
    }.value as android.graphics.Typeface

    AndroidView(factory = { context ->
        TextView(context).apply {
            setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
            textSize = textSizeBody
            setTextColor(textColorBody.toArgb())
            typeface = typefaceBody
        }
    })
}

@Composable
private fun HtmlBodyItalic(text: String) {
    val textSizeBody = AppTheme.typography.body.fontSize.value
    val textColorBody = AppTheme.colorScheme.secondary
    val textFontBody = AppTheme.typography.bodyItalic

    val resolver: FontFamily.Resolver = LocalFontFamilyResolver.current
    val typefaceBody: android.graphics.Typeface = remember(resolver, textFontBody) {
        resolver.resolve(
            fontFamily = textFontBody.fontFamily
        )
    }.value as android.graphics.Typeface

    AndroidView(factory = { context ->
        TextView(context).apply {
            setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
            textSize = textSizeBody
            setTextColor(textColorBody.toArgb())
            typeface = typefaceBody
        }
    })
}

private fun addItalicStyleToPsalm(text: String): String {
    val substring = "Odpev</p>"

    val pattern: Pattern = Pattern.compile(substring)
    val matcher: Matcher = pattern.matcher(text)
    val modifiedText = StringBuilder()

    var lastIndex = 0
    while (matcher.find()) {
        val startIndex = matcher.start()
        val endIndex = matcher.end()

        modifiedText.append(text.substring(lastIndex, startIndex))
        modifiedText.append("<i>")
        modifiedText.append(text.substring(startIndex,endIndex))
        modifiedText.append("</i>")
        lastIndex = endIndex
    }
    modifiedText.append(text.substring(lastIndex))
    return modifiedText.toString()
}
private fun addBreakAfterSourceOfAleluja(text: String): String {
    val substring = ')'.toString()
    val index = text.indexOf(substring)
    if (index != -1) {
        val modifiedText = StringBuilder(text)
        modifiedText.insert(index + 1, "<br>")
        return modifiedText.toString()
    }
    return text

}
private fun createSelektor(viewModel: LekcionarViewModel) {
    viewModel.updateSelectedRed("kapucini")
    viewModel.updateSelectedSkofija("slovenija")
}