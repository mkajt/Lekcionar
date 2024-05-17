package mkajt.hozana.lekcionar.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mkajt.hozana.lekcionar.mediaPlayer.MediaPlayerState
import mkajt.hozana.lekcionar.model.LekcionarRepository
import mkajt.hozana.lekcionar.model.dataStore.DataStoreManager
import mkajt.hozana.lekcionar.model.database.PodatkiEntity
import mkajt.hozana.lekcionar.util.isInternetAvailable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LekcionarViewModel(
    application: Application?,
    private val lekcionarRepository: LekcionarRepository
) :
    AndroidViewModel(application!!) {

     private val _dataState = MutableStateFlow<LekcionarViewState>(LekcionarViewState.Start)
    val dataState: StateFlow<LekcionarViewState> = _dataState.asStateFlow()

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val _selektor = MutableStateFlow("")

    private val _selectedDate = MutableStateFlow(dateFormatter.format(LocalDate.now()))
    val selectedDate = _selectedDate.asStateFlow()
    private val _selectedRed = MutableStateFlow("kapucini")
    private val _selectedSkofija = MutableStateFlow("slovenija")

    private val _smallestTimestamp = MutableStateFlow(0L)
    //val smallestTimestamp = _smallestTimestamp.asStateFlow()
    private val _biggestTimestamp = MutableStateFlow(0L)
    //val biggestTimestamp = _biggestTimestamp.asStateFlow()

    private val _idPodatek = MutableStateFlow<List<String>?>(null)
    val idPodatek = _idPodatek.asStateFlow()
    private val _podatki = MutableStateFlow<List<PodatkiEntity>?>(null)
    val podatki = _podatki.asStateFlow()

    private val dataStore : DataStoreManager
    private val isDarkTheme: StateFlow<Boolean>
    private val updatedDataTimestamp: StateFlow<Long>
    val firstDataTimestamp: StateFlow<Long>
    val lastDataTimestamp: StateFlow<Long>

    private var _mediaPlayerState = MutableStateFlow(MediaPlayerState())
    val mediaPlayerState = _mediaPlayerState.asStateFlow()



    init {
        val context: Context = getApplication<Application>().applicationContext
        dataStore = DataStoreManager(context)
        isDarkTheme = dataStore.getTheme().stateIn(viewModelScope, SharingStarted.Lazily, false)
        updatedDataTimestamp = dataStore.getUpdatedDataTimestamp().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
        firstDataTimestamp = dataStore.getFirstDataTimestamp().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
        lastDataTimestamp = dataStore.getLastDataTimestamp().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
    }


    fun toggleIsDarkTheme() {
        viewModelScope.launch {
            val toggleTheme: Boolean = !isDarkTheme.value
            dataStore.setTheme(toggleTheme)
        }
    }

    fun getFirstDataTimestamp(): Long {
        return firstDataTimestamp.value
    }

    private suspend fun setFirstDataTimestamp(timestamp: Long) {
        dataStore.setFirstDataTimestamp(timestamp)
        /*viewModelScope.launch {
        }*/
    }

    fun getLastDataTimestamp(): Long {
        return lastDataTimestamp.value
    }

    private suspend fun setLastDataTimestamp(timestamp: Long) {
        dataStore.setLastDataTimestamp(timestamp)
        /*viewModelScope.launch {
            dataStore.setLastDataTimestamp(timestamp)
        }*/
    }

    fun getUpdatedDataTimestamp(): Long {
        return  updatedDataTimestamp.value
    }


    fun checkDbAndFetchDataFromApi() {
        viewModelScope.launch {
            try {
                val count = lekcionarRepository.countPodatki()
                if (count == 0) {
                    if (!isInternetAvailable(context = getApplication<Application>().applicationContext)) {
                        _dataState.update { LekcionarViewState.NoInternet }
                        return@launch
                    } else {
                        _dataState.update { LekcionarViewState.Loading }
                        val updatedTimestamp= lekcionarRepository.getLekcionarDataFromApi()
                        if (updatedTimestamp == 0L) {
                            throw Exception("Error occurred while downloading data.")
                        }
                        dataStore.setUpdatedDataTimestamp(updatedTimestamp)
                        _dataState.update { LekcionarViewState.Loaded }
                    }
                }
                _dataState.update { LekcionarViewState.AlreadyInDb }
                _smallestTimestamp.update { lekcionarRepository.getSmallestTimestamp() }
                _biggestTimestamp.update { lekcionarRepository.getBiggestTimestamp() }
                Log.d("LVM", "SmallestTimestamp: " + _smallestTimestamp.value)
                Log.d("LVM", "BiggestTimestamp: " + _biggestTimestamp.value)
                //setFirstDataTimestamp(_smallestTimestamp.value)
                //setLastDataTimestamp(_biggestTimestamp.value)
                dataStore.setFirstDataTimestamp(_smallestTimestamp.value)
                dataStore.setLastDataTimestamp(_biggestTimestamp.value)

                Log.d("LVM", "FirstTimestamp: " + firstDataTimestamp.value)
                Log.d("LVM", "LastTimestamp: " + lastDataTimestamp.value)
                getPodatkiBySelektor()

            } catch (e: Exception) {
                _dataState.update { LekcionarViewState.Error(e.message ?: "Unknown error") }
            }
        }
    }

    private fun updateSelektor() {
        _selektor.update { "${_selectedDate.value}-${_selectedSkofija.value}-${_selectedRed.value}" }
        Log.d("DATE", _selektor.value)
        getPodatkiBySelektor()
    }

    fun updateSelectedDate(date: LocalDate) {
        if (dateFormatter.format(date) != _selectedDate.value) {
            _selectedDate.update { dateFormatter.format(date) }
            updateSelektor()
        }
    }

    fun updateSelectedRed(red: String) {
        _selectedRed.update { red }
        updateSelektor()
    }

    fun updateSelectedSkofija(skofija: String) {
        _selectedSkofija.update { skofija }
        updateSelektor()
    }

    fun goToNextDate() {
        val currentDate = LocalDate.parse(_selectedDate.value, dateFormatter)
        updateSelectedDate(currentDate.plusDays(1))
    }

    fun goToPreviousDate() {
        val currentDate = LocalDate.parse(_selectedDate.value, dateFormatter)
        updateSelectedDate(currentDate.minusDays(1))
    }


    private fun getPodatkiBySelektor() {
        viewModelScope.launch {
            if (_selektor.value != "") {
                val ids = lekcionarRepository.getIdPodatekFromMap(_selektor.value)
                if (ids.isNullOrEmpty()) {
                    return@launch
                }
                _idPodatek.update { ids.split(",") }

                val podatki = lekcionarRepository.getPodatki(_idPodatek.value!!)
                _podatki.update { podatki }
                Log.d("LVM", "Podatki: ${_podatki.value}")
            }
        }
    }

    fun updateMediaPlayerState(state: MediaPlayerState) {
        _mediaPlayerState.update {
            it.copy(
                isPlaying = state.isPlaying,
                isStopped = state.isStopped,
                currentPosition = state.currentPosition,
                duration = state.duration,
                title = state.title,
                uri = state.uri
            )
        }
    }

}