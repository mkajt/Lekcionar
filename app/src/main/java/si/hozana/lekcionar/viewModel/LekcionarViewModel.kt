package si.hozana.lekcionar.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import si.hozana.lekcionar.mediaPlayer.MediaPlayerState
import si.hozana.lekcionar.model.LekcionarRepository
import si.hozana.lekcionar.model.dataStore.DataStoreManager
import si.hozana.lekcionar.model.database.PodatkiEntity
import si.hozana.lekcionar.model.database.RedEntity
import si.hozana.lekcionar.model.database.SkofijaEntity
import si.hozana.lekcionar.util.isInternetAvailable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LekcionarViewModel(
    application: Application?,
    private val lekcionarRepository: LekcionarRepository,
) : AndroidViewModel(application!!) {

    private val _dataState = MutableStateFlow<LekcionarViewState>(LekcionarViewState.Start)
    val dataState: StateFlow<LekcionarViewState> = _dataState.asStateFlow()

    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val _selektor = MutableStateFlow("")

    private val _selectedDate = MutableStateFlow(dateFormatter.format(LocalDate.now()))
    val selectedDate = _selectedDate.asStateFlow()

    private val _idPodatek = MutableStateFlow<List<String>?>(null)
    private val _podatki = MutableStateFlow<List<PodatkiEntity>?>(null)
    val podatki = _podatki.asStateFlow()

    private val dataStore : DataStoreManager
    val isDarkTheme: StateFlow<Boolean>
    val updatedDataTimestamp: StateFlow<Long>
    val firstDataTimestamp: StateFlow<Long>
    val lastDataTimestamp: StateFlow<Long>
    val selectedRed: StateFlow<String>
    val selectedSkofija: StateFlow<String>
    val testUpdate: StateFlow<Long> //TODO delete before release

    private val _redList = MutableStateFlow<List<RedEntity>?>(null)
    val redList = _redList.asStateFlow()
    private val _skofijaList = MutableStateFlow<List<SkofijaEntity>?>(null)
    val skofijaList = _skofijaList.asStateFlow()

    private var _mediaPlayerState = MutableStateFlow(MediaPlayerState())
    val mediaPlayerState = _mediaPlayerState.asStateFlow()

    init {
        val context: Context = getApplication<Application>().applicationContext
        dataStore = DataStoreManager(context)
        isDarkTheme = dataStore.getTheme().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), runBlocking { dataStore.getTheme().first() })
        updatedDataTimestamp = dataStore.getUpdatedDataTimestamp().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), runBlocking { dataStore.getUpdatedDataTimestamp().first() })
        firstDataTimestamp = dataStore.getFirstDataTimestamp().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), runBlocking { dataStore.getFirstDataTimestamp().first() })
        lastDataTimestamp = dataStore.getLastDataTimestamp().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), runBlocking { dataStore.getLastDataTimestamp().first() })
        selectedRed = dataStore.getRed().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), runBlocking { dataStore.getRed().first() })
        selectedSkofija = dataStore.getSkofija().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), runBlocking { dataStore.getSkofija().first() })
        testUpdate = dataStore.getTestUpdateTimestamp().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), runBlocking { dataStore.getTestUpdateTimestamp().first() })
    }

    fun getIsDarkTheme(): Boolean {
        return isDarkTheme.value
    }

    fun setIsDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            dataStore.setTheme(isDark)
        }
    }

    fun getFirstDataTimestamp(): Long {
        return firstDataTimestamp.value
    }

    fun getLastDataTimestamp(): Long {
        return lastDataTimestamp.value
    }

    fun getUpdatedDataTimestamp(): Long {
        return  updatedDataTimestamp.value
    }

    fun getSelectedRed(): String {
        return selectedRed.value
    }

    fun setSelectedRed(red: String) {
        viewModelScope.launch {
            dataStore.setRed(red)
            updateSelektor()
        }
    }

    fun getSelectedSkofija(): String {
        return selectedSkofija.value
    }

    fun setSelectedSkofija(skofija: String) {
        viewModelScope.launch {
            dataStore.setSkofija(skofija)
            updateSelektor()
        }
    }

    fun getTestUpdate(): Long { //TODO delete
        return testUpdate.value
    }

    private fun updateSelektor() {
        _selektor.update { "${_selectedDate.value}-${selectedSkofija.value}-${selectedRed.value}" }
        getPodatkiBySelektor()
    }

    fun updateSelectedDate(date: LocalDate) {
        if (dateFormatter.format(date) != _selectedDate.value) {
            _selectedDate.update { dateFormatter.format(date) }
            updateSelektor()
        }
    }

    fun goToNextDate() {
        val currentDate = LocalDate.parse(_selectedDate.value, dateFormatter)
        updateSelectedDate(currentDate.plusDays(1))
    }

    fun goToPreviousDate() {
        val currentDate = LocalDate.parse(_selectedDate.value, dateFormatter)
        updateSelectedDate(currentDate.minusDays(1))
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
                        val updatedTimestamp = lekcionarRepository.getLekcionarDataFromApi()
                        if (updatedTimestamp == 0L) {
                            throw Exception("Error occurred while downloading data.")
                        }
                        dataStore.setUpdatedDataTimestamp(updatedTimestamp)
                        _dataState.update { LekcionarViewState.Loaded }
                        val smallestTimestamp = lekcionarRepository.getFirstDataTimestamp()
                        val biggestTimestamp = lekcionarRepository.getLastDataTimestamp()
                        dataStore.setFirstDataTimestamp(smallestTimestamp)
                        dataStore.setLastDataTimestamp(biggestTimestamp)
                    }
                }
                _dataState.update { LekcionarViewState.AlreadyInDb }

                val red = lekcionarRepository.getRedList()
                _redList.update { red }

                val skofija = lekcionarRepository.getSkofijaList()
                _skofijaList.update { skofija }

                updateSelektor()
                getPodatkiBySelektor()
            } catch (e: Exception) {
                _dataState.update { LekcionarViewState.Error(e.message ?: "Unknown error") }
            }
        }
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