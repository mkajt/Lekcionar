package mkajt.hozana.lekcionar.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mkajt.hozana.lekcionar.ui.theme.AppTheme

@Composable
fun DisplayStatus(state: String, modifier: Modifier, retry: () -> Unit) {
    var displayStatus = ""
    when (state) {
        "NoInternet" -> {
            displayStatus = "Za prenos podatkov je potrebna internetna povezava!"
        }
        "Error" -> {
            displayStatus = "Prišlo je do napake."
        }
        "Loading" -> {
            displayStatus = "Nalaganje podatkov..."
        }
    }

    Column(modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Center
            ) {
            Text(
                text = displayStatus,
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        if (state == "NoInternet" || state == "Error") {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
                horizontalArrangement = Arrangement.Center) {
                OutlinedButton(
                    onClick = retry,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AppTheme.colorScheme.primary,
                        contentColor = AppTheme.colorScheme.background,
                    ),
                    shape = AppTheme.shape.button,
                    border = BorderStroke(2.dp, AppTheme.colorScheme.primary)
                ) {
                    Text(text = "Poskusi znova", style = AppTheme.typography.labelLarge, textAlign = TextAlign.Center)
                }
            }
        }
    }

}