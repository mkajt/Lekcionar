package si.kapucini.lekcionar.ui.routes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import si.kapucini.lekcionar.ActivityListener
import si.kapucini.lekcionar.ui.components.AppInfo
import si.kapucini.lekcionar.ui.components.Calendar
import si.kapucini.lekcionar.ui.components.Home
import si.kapucini.lekcionar.ui.components.Settings
import si.kapucini.lekcionar.viewModel.LekcionarViewModel

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
        composable(Screen.CALLENDAR.name) { Calendar(viewModel = viewModel, navController = navController, activityListener = activityListener)}
        composable(Screen.SETTINGS.name) { Settings(viewModel = viewModel, navController = navController)}
        composable(Screen.APPINFO.name) { AppInfo(viewModel = viewModel, navController = navController)}
    }
}