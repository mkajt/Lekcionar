package mkajt.hozana.lekcionar.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    fun fetchDataFromApi() {
        viewModelScope.launch {
            _dataState.value =  LekcionarViewState.Loading
            lekcionarRepository.getLekcionarDataFromApi()
            _dataState.value = LekcionarViewState.Loaded
        }
    }

    fun updateSelektor(newSelektor: String) {
        _selektor.value = newSelektor
    }

    fun getPodatkiBySelektor() {
        if (_selektor.value != "") {
            viewModelScope.launch {
                _idPodatek.value = lekcionarRepository.getIdPodatekFromMap(_selektor.value)
                _podatki.value = lekcionarRepository.getPodatki(_idPodatek.value)
                Log.d("LVM", "Podatki: ${_podatki.value}")
            }
        }
    }
}