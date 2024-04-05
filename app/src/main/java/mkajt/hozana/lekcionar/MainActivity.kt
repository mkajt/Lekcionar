package mkajt.hozana.lekcionar

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import mkajt.hozana.lekcionar.model.LekcionarRepository
import mkajt.hozana.lekcionar.model.database.LekcionarDB
import mkajt.hozana.lekcionar.ui.components.HomeSection
import mkajt.hozana.lekcionar.ui.theme.AppTheme
import mkajt.hozana.lekcionar.viewModel.LekcionarViewModel

class MainActivity : ComponentActivity() {

    private lateinit var lekcionarViewModel: LekcionarViewModel
    private lateinit var lekcionarRepository: LekcionarRepository
    private lateinit var lekcionarDB: LekcionarDB
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = application.applicationContext

        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = AppTheme.colorScheme.background
                ) {
                    context = LocalContext.current
                    lekcionarDB = LekcionarDB.getInstance(context)
                    lekcionarRepository = LekcionarRepository(context, lekcionarDB, Dispatchers.IO)
                    lekcionarViewModel = LekcionarViewModel(application, lekcionarRepository)
                    lekcionarViewModel.checkDbAndfetchDataFromApi()

                    HomeSection(lekcionarViewModel)
                }
            }
        }
    }

}