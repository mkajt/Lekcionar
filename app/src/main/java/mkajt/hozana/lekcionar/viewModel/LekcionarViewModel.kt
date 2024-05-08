package mkajt.hozana.lekcionar.viewModel

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mkajt.hozana.lekcionar.mediaPlayer.MediaPlayerEvent
import mkajt.hozana.lekcionar.mediaPlayer.MediaPlayerState
import mkajt.hozana.lekcionar.model.LekcionarRepository
import mkajt.hozana.lekcionar.model.dataStore.DataStoreManager
import mkajt.hozana.lekcionar.model.database.PodatkiEntity
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

    private var _player: MediaPlayer? = null
    private var _mediaPlayerState = MutableStateFlow(MediaPlayerState())
    val mediaPlayerState = _mediaPlayerState.asStateFlow()
    private val _handler = Handler(Looper.getMainLooper())

    //private val notificationManager: NotificationMng

    init {
        val context: Context = getApplication<Application>().applicationContext
        //notificationManager = ContextCompat.getSystemService(context, NotificationMng(context, this)::class.java)!!
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
                    _dataState.update { LekcionarViewState.Loading }
                    val updatedTimestamp = lekcionarRepository.getLekcionarDataFromApi()
                    dataStore.setUpdatedDataTimestamp(updatedTimestamp)
                    _dataState.update { LekcionarViewState.Loaded }
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
                val ids = async { lekcionarRepository.getIdPodatekFromMap(_selektor.value) }
                val idsString = ids.await() ?: return@launch
                _idPodatek.update { idsString.split(",") }
                //Log.d("LVM", "Id podatek: ${_idPodatek.value}")

                val podatki = async { lekcionarRepository.getPodatki(_idPodatek.value!!) }
                _podatki.update { podatki.await() }
                //_podatki.value = podatki.await()
                Log.d("LVM", "Podatki: ${_podatki.value}")
            }
        }
    }

    fun updateMediaPlayerState(state: MediaPlayerState) {
        _mediaPlayerState.update {
            it.copy(
                isPlaying = state.isPlaying,
                currentPosition = state.currentPosition,
                duration = state.duration,
                title = state.title,
                uri = state.uri
            )
        }
    }

    fun onMediaPlayerEvent(event: MediaPlayerEvent) {
        when (event) {
            is MediaPlayerEvent.Initialize -> initPlayer(event.uri, event.context)
            is MediaPlayerEvent.Seek -> seek(event.position)
            is MediaPlayerEvent.Play -> play()
            is MediaPlayerEvent.Pause -> pause()
            is MediaPlayerEvent.Stop -> stop()
        }
    }
    private fun initPlayer(uri: Uri, context: Context) {
        if (_player == null || _player?.duration!! == 0) {
            viewModelScope.launch {
                _player = MediaPlayer().apply {
                    setDataSource(context, uri)
                    setOnErrorListener { mediaPlayer, what, extra ->
                        Log.e("MediaPlayer", "Error occurred while preparing media source: $what; extra: $extra")
                        false
                    }
                    setOnPreparedListener{
                        play()
                    }
                    prepareAsync()
                }
            }
        } else {
            play()
        }
    }
    private fun play() {
        _mediaPlayerState.update {
            it.copy(
                isPlaying = true,
                duration = _player?.duration!!)
        }

        //Log.d("LVM", _mediaPlayerState.value.duration.toString())

        _player?.seekTo(_mediaPlayerState.value.currentPosition)
        _player?.start()
        _handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    _mediaPlayerState.update {
                        it.copy(currentPosition = _player?.currentPosition!!)
                    }
                    _handler.postDelayed(this, 1000)
                    //Log.d("HANDLER", _mediaPlayerState.value.currentPosition.toString())
                } catch (e: Exception) {
                    _mediaPlayerState.update {
                        it.copy(currentPosition = 0)
                    }
                    e.printStackTrace()
                }
            }
        }, 0)

    }
    private fun pause() {
        _mediaPlayerState.update {
            it.copy(
                isPlaying = false,
                currentPosition = _player?.currentPosition ?: 0)
        }
        _player?.pause()
        _handler.removeMessages(0)
    }
    private fun stop() {
        _mediaPlayerState.update {
            it.copy(
                isPlaying = false,
                currentPosition = 0,
                duration = 0
            )
        }
        _player?.stop()
        _player?.reset()
        _player?.release()
        _player = null
    }
    private fun seek(position: Float){
        _mediaPlayerState.update {
            it.copy(
                currentPosition = position.toInt()
            )
        }
        _player?.seekTo(position.toInt())
    }

}