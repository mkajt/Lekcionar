package mkajt.hozana.lekcionar.ui.routes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mkajt.hozana.lekcionar.ActivityListener
import mkajt.hozana.lekcionar.ui.components.Calendar
import mkajt.hozana.lekcionar.ui.components.Home
import mkajt.hozana.lekcionar.viewModel.LekcionarViewModel

enum class Screen {
    HOME,
    CALLENDAR,
    APPINFO,
    SETTINGS
}

@Composable
fun AppNavigation(viewModel: LekcionarViewModel, activityListener: ActivityListener) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.HOME.name) {
        composable(Screen.HOME.name) { Home(viewModel = viewModel, navController = navController, actListener = activityListener )}
        composable(Screen.CALLENDAR.name) { Calendar(viewModel = viewModel, navController = navController, actListener = activityListener)}
    }
}