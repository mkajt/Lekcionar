package mkajt.hozana.lekcionar.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mkajt.hozana.lekcionar.model.LekcionarRepository
import mkajt.hozana.lekcionar.model.database.PodatkiEntity

class LekcionarViewModel(
    application: Application?,
    private val lekcionarRepository: LekcionarRepository
) :
    AndroidViewModel(application!!) {

    private val _dataState = MutableStateFlow<LekcionarViewState>(LekcionarViewState.Start)
    val dataState: StateFlow<LekcionarViewState> = _dataState.asStateFlow()

    private val _selektor = MutableStateFlow("")
    private val _idPodatek = MutableStateFlow("")
    val idPodatek = _idPodatek.asStateFlow()
    private val _podatki = MutableStateFlow<PodatkiEntity?>(null)
    val podatki = _podatki.asStateFlow()

    fun checkDbAndfetchDataFromApi() {
        viewModelScope.launch {
            try {
                val count = lekcionarRepository.countPodatki()
                if (count == 0) {
                    _dataState.value = LekcionarViewState.Loading
                    lekcionarRepository.getLekcionarDataFromApi()
                    _dataState.value = LekcionarViewState.Loaded
                } else {
                    _dataState.value = LekcionarViewState.AlreadyInDb
                }
            } catch (e: Exception) {
                _dataState.value = LekcionarViewState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateSelektor(newSelektor: String) {
        _selektor.value = newSelektor
    }

    fun getPodatkiBySelektor() {
        if (_selektor.value != "") {
            viewModelScope.launch {
                val id = async { lekcionarRepository.getIdPodatekFromMap(_selektor.value) }
                _idPodatek.value = id.await()
                val podatki = async { lekcionarRepository.getPodatki(_idPodatek.value) }
                _podatki.value = podatki.await()
                Log.d("LVM", "Podatki: ${_podatki.value}")
            }
        }
    }
}