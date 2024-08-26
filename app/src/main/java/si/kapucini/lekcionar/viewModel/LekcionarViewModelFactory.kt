package si.kapucini.lekcionar.viewModel

import android.app.Application
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import si.kapucini.lekcionar.model.LekcionarRepository


class LekcionarViewModelFactory(
    private val application: Application,
    private val lekcionarRepository: LekcionarRepository,
    owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(LekcionarViewModel::class.java)) {
            return LekcionarViewModel(application, lekcionarRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}