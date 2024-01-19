package mkajt.hozana.lekcionar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import mkajt.hozana.lekcionar.ui.components.UserInterface
import mkajt.hozana.lekcionar.ui.theme.LekcionarTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LekcionarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    UserInterface()
                }
            }
        }
    }
}