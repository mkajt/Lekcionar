package mkajt.hozana.lekcionar.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mkajt.hozana.lekcionar.model.LekcionarRepository
import mkajt.hozana.lekcionar.model.dto.MapDTO
import mkajt.hozana.lekcionar.model.dto.PodatkiDTO
import mkajt.hozana.lekcionar.model.dto.RedDTO
import mkajt.hozana.lekcionar.model.dto.SkofijaDTO

class LekcionarViewModel(application: Application?): AndroidViewModel(application!!) {

    private val lekcionarRepository: LekcionarRepository
    var redovi: List<RedDTO>? = null
    var skofije: List<SkofijaDTO>? = null
    var map: List<MapDTO>? = null
    var podatki: List<PodatkiDTO>? = null

    /*fun getLekcionarData() {
        // coroutine viewmodel scope to call suspend fun of repo
        viewModelScope.launch { lekcionarRepository.getLekcionarData() }
    }*/

    init {
        lekcionarRepository = LekcionarRepository(application)
        viewModelScope.launch {
            lekcionarRepository.getLekcionarData()
        }
    }
}