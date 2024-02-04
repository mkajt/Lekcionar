package mkajt.hozana.lekcionar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import mkajt.hozana.lekcionar.ui.components.UserInterface
import mkajt.hozana.lekcionar.ui.theme.LekcionarTheme
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            // TODO: @maja popravi url na svoj naslov
            .baseUrl("https://example.com/")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()

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