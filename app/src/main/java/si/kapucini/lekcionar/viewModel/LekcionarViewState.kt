package si.kapucini.lekcionar.viewModel

sealed class LekcionarViewState {

    object Loading : LekcionarViewState()

    object Loaded : LekcionarViewState()

    object Start : LekcionarViewState()

    object AlreadyInDb : LekcionarViewState()

    object NoInternet : LekcionarViewState()

    data class Error(val errorMessage: String) : LekcionarViewState()

}