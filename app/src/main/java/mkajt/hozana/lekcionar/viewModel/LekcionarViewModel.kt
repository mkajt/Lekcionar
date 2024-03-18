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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val _idPodatek = MutableStateFlow<List<String>?>(null)
    val idPodatek = _idPodatek.asStateFlow()
    private val _podatki = MutableStateFlow<List<PodatkiEntity>?>(null)
    val podatki = _podatki.asStateFlow()

    private var _player: MediaPlayer? = null
    private var _mediaPlayerState = MutableStateFlow(MediaPlayerState())
    val mediaPlayerState = _mediaPlayerState.asStateFlow()
    private val _handler = Handler(Looper.getMainLooper())

    fun checkDbAndfetchDataFromApi() {
        viewModelScope.launch {
            try {
                val count = lekcionarRepository.countPodatki()
                if (count == 0) {
                    _dataState.value = LekcionarViewState.Loading
                    lekcionarRepository.getLekcionarDataFromApi()
                    _dataState.value = LekcionarViewState.Loaded
                }
                _dataState.value = LekcionarViewState.AlreadyInDb

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
                val ids = async { lekcionarRepository.getIdPodatekFromMap(_selektor.value) }
                _idPodatek.value = ids.await()
                val podatki = async { lekcionarRepository.getPodatki(_idPodatek.value!!) }
                _podatki.update {podatki.await()}
                //_podatki.value = podatki.await()
                Log.d("LVM", "Podatki: ${_podatki.value}")
            }
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
        if (_player == null) {
            viewModelScope.launch {
                _player = MediaPlayer().apply {
                    setDataSource(context, uri)
                    prepare()
                }
                play()
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