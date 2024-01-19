package mkajt.hozana.lekcionar.ui.routes

sealed class ScreenRoutes(val route: String) {
    object AppInfo : ScreenRoutes("/info")
    object Settings : ScreenRoutes("/settings")
    object Home : ScreenRoutes("/home")
    object Callendar : ScreenRoutes("/callendar")
}
/*
fun navigateTo(route: ScreenRoutes) {
    when (route) {
        is ScreenRoutes.AppInfo -> /* navigate to app info screen */
        is ScreenRoutes.Settings -> /* navigate to settings screen */
        is ScreenRoutes.Home -> /* navigate to home screen */
        is ScreenRoutes.Callendar -> /* navigate to calendar screen */
    }
}
*/