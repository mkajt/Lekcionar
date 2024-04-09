package mkajt.hozana.lekcionar.ui.routes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mkajt.hozana.lekcionar.ui.components.HomeSection
import mkajt.hozana.lekcionar.viewModel.LekcionarViewModel

enum class Screen {
    HOME,
    CALLENDAR,
    APPINFO,
    SETTINGS
}

@Composable
fun AppNavigation(viewModel: LekcionarViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.HOME.name) {
        composable(Screen.HOME.name) { HomeSection(viewModel = viewModel, navController = navController)}
    }
}