package mkajt.hozana.lekcionar.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import mkajt.hozana.lekcionar.model.LekcionarRepository
import mkajt.hozana.lekcionar.model.dto.LekcionarDTO

class LekcionarViewModel: ViewModel() {
    private val lekcionarRepository = LekcionarRepository()
    private val _state = MutableStateFlow<LekcionarDTO?>(null)
    val state: StateFlow<LekcionarDTO?>
        get() = _state.asStateFlow()

    //val lekcionarData = lekcionarRepository.lekcionarData

    fun getLekcionarData() {
        // coroutine viewmodel scope to call suspend fun of repo
        viewModelScope.launch { lekcionarRepository.getLekcionarData() }
    }
}