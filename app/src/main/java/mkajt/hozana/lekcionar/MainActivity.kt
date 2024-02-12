package mkajt.hozana.lekcionar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import mkajt.hozana.lekcionar.model.LekcionarRepository
import mkajt.hozana.lekcionar.ui.components.UserInterface
import mkajt.hozana.lekcionar.ui.theme.LekcionarTheme
import mkajt.hozana.lekcionar.viewModel.LekcionarViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class MainActivity : ComponentActivity() {

    private lateinit var lekcionarViewModel: LekcionarViewModel
    private lateinit var lekcionarRepository: LekcionarRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lekcionarViewModel = ViewModelProvider(this)[LekcionarViewModel::class.java]

        setContent {
            LekcionarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    UserInterface(lekcionarViewModel)
                }
            }
        }
    }

}