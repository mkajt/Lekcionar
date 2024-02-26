package mkajt.hozana.lekcionar

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import mkajt.hozana.lekcionar.model.LekcionarRepository
import mkajt.hozana.lekcionar.model.database.LekcionarDB
import mkajt.hozana.lekcionar.ui.components.UserInterface
import mkajt.hozana.lekcionar.ui.theme.LekcionarTheme
import mkajt.hozana.lekcionar.viewModel.LekcionarViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class MainActivity : ComponentActivity() {

    private lateinit var lekcionarViewModel: LekcionarViewModel
    private lateinit var lekcionarRepository: LekcionarRepository
    private lateinit var lekcionarDB: LekcionarDB
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = application.applicationContext

        setContent {
            LekcionarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    context = LocalContext.current
                    lekcionarDB = LekcionarDB.getInstance(context)
                    lekcionarRepository = LekcionarRepository(context, lekcionarDB, Dispatchers.IO)
                    lekcionarViewModel = LekcionarViewModel(application, lekcionarRepository)
                    lekcionarViewModel.fetchDataFromApi()

                    UserInterface(lekcionarViewModel)
                }
            }
        }
    }

}