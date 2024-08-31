package si.hozana.lekcionar.mediaPlayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import si.hozana.lekcionar.MainActivity
import si.hozana.lekcionar.R
import si.hozana.lekcionar.viewModel.LekcionarViewModel

class MediaPlayerService: Service() {

    private lateinit var viewModel: LekcionarViewModel

    companion object {
        private val TAG: String? = MediaPlayerService::class.simpleName
        private const val channelID = "background_player"
        const val ACTION_START = "start_service"
        const val ACTION_STOP = "stop_service"
        const val ACTION_PAUSE = "pause_service"
        const val ACTION_SEEK = "seek_to_service"
        const val ACTION_EXIT = "exit_service"
        const val NOTIFICATION_ID = 11
    }

    // allows communication with service
    inner class RunServiceBinder : Binder() {
        val service: MediaPlayerService
            get() = this@MediaPlayerService
    }

    private var serviceBinder: Binder = RunServiceBinder()

    var context: Context? = null

    var mediaPlayer: MediaPlayer? = null
    var mediaPlayerState = MediaPlayerState()

    private val handler = Handler(Looper.getMainLooper())

    //if start or pause button was clicked
    var started: Boolean = false
    var paused: Boolean = false

    // notification manager
    private var notificationManagerCompat: NotificationManagerCompat? = null
    private lateinit var notificationManager: NotificationManager

    fun injectViewModel(lekcionarViewModel: LekcionarViewModel) {
        viewModel = lekcionarViewModel
    }

    override fun onCreate() {
        Log.d(TAG, "Creating service")

        context = this

        mediaPlayerState = MediaPlayerState()
        started = false
        paused = false
        mediaPlayer = MediaPlayer()

        notificationManagerCompat = NotificationManagerCompat.from(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //null validation for error: Caused by: java.lang.NullPointerException: Parameter specified as non-null is null: method .onStartCommand, parameter intent
        if (intent != null) {
            when (intent.action) {
                ACTION_START -> {
                    val uri = intent.getStringExtra("uri")
                    val opis = intent.getStringExtra("opis")
                    if (uri != null) {
                        playInit(Uri.parse(uri), opis!!)
                    }
                }
                ACTION_STOP -> {
                    stop()
                }
                ACTION_PAUSE -> {
                    pause()
                }
                ACTION_SEEK -> {
                    val seekTo = intent.getFloatExtra("seekTo", 0F)
                    seek(seekTo)
                }
                ACTION_EXIT -> {
                    stop()
                    exit()
                }
            }
        }

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder {
        Log.d(TAG, "Binding service")
        return serviceBinder
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stop()
        exit()
    }

    private var mediaRunnable = object : Runnable {
        override fun run() {
            try {
                mediaPlayerState.currentPosition = mediaPlayer?.currentPosition!!
                viewModel.updateMediaPlayerState(mediaPlayerState)
                if (getTime(mediaPlayerState.currentPosition) == getTime(mediaPlayerState.duration) && mediaPlayerState.duration != 0) {
                    stop()
                }
                if (notificationManager.activeNotifications.any{it.id == NOTIFICATION_ID}) {
                    foreground()
                }
                handler.postDelayed(this, 1000)
            } catch (e: Exception) {
                mediaPlayerState.currentPosition = 0
                viewModel.updateMediaPlayerState(mediaPlayerState)
                e.printStackTrace()
            }
        }
    }

    fun playInit(uri: Uri, opis: String) {
        if (opis != mediaPlayerState.title) {
            mediaPlayerState.currentPosition = 0
            mediaPlayerState.duration = 0
            mediaPlayerState.isPlaying = false
            mediaPlayerState.uri = uri
            mediaPlayerState.title = opis
        }
        viewModel.updateMediaPlayerState(mediaPlayerState)
        if (!started || !paused) {
            mediaPlayer?.reset()
            mediaPlayer?.apply {
                context?.let { mediaPlayerState.uri?.let { it1 -> setDataSource(it, it1) } }
                setOnErrorListener { mediaPlayer, what, extra ->
                    Log.e("MediaPlayer", "Error occurred while preparing media source: $what; extra: $extra")
                    false
                }
                setOnPreparedListener{
                    play()
                }
                prepareAsync()
            }
        } else {
            play()
        }
    }

    private fun play() {
        mediaPlayerState.isPlaying = true
        mediaPlayerState.isStopped = false
        mediaPlayerState.duration = mediaPlayer?.duration!!
        mediaPlayer?.seekTo(mediaPlayerState.currentPosition)
        mediaPlayer?.start()

        handler.postDelayed(mediaRunnable, 0)

        paused = false
        started = true
    }

    fun stop() {
        if (started) {
            handler.postDelayed({
                handler.removeCallbacks(mediaRunnable)
            }, 100)

            mediaPlayerState.isPlaying = false
            mediaPlayerState.isStopped = true
            mediaPlayerState.currentPosition = 0
            viewModel.updateMediaPlayerState(mediaPlayerState)

            mediaPlayer?.stop()

            started = false
            paused = false

            if (notificationManager.activeNotifications.any{it.id == NOTIFICATION_ID}) {
                foreground()
            }
        }
    }

    fun pause() {
        if (started && !paused) {
            mediaPlayerState.isPlaying = false
            mediaPlayerState.currentPosition = mediaPlayer?.currentPosition ?: 0
            paused = true
        }
        viewModel.updateMediaPlayerState(mediaPlayerState)
        mediaPlayer?.pause()

        handler.postDelayed({
            handler.removeCallbacks(mediaRunnable)
        }, 100)

        if (notificationManager.activeNotifications.any{it.id == NOTIFICATION_ID}) {
            foreground()
        }
    }

    fun seek(position: Float) {
        mediaPlayerState.currentPosition = position.toInt()
        viewModel.updateMediaPlayerState(mediaPlayerState)
        mediaPlayer?.seekTo(mediaPlayerState.currentPosition)
    }

    fun exit() {
        if (started && mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        started = false
        paused = false

        notificationManagerCompat?.cancel(NOTIFICATION_ID)
        stopSelf()
    }

    // create a notification channel for the foreground service
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT > 26) {
            val channel = NotificationChannel(channelID, "Foreground channel", NotificationManager.IMPORTANCE_LOW)
            channel.description = "Notifications about mediaPlayerService"
            channel.enableLights(false)
            channel.enableVibration(false)
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {

        val actionIntentStartPause = Intent(this, MediaPlayerService::class.java)
        lateinit var actionPendingIntentStartPause: PendingIntent

        val actionIntentExit = Intent(this, MediaPlayerService::class.java)
        actionIntentExit.action = ACTION_EXIT
        val actionPendingIntentExit = PendingIntent.getService(this, 0, actionIntentExit, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        if (mediaPlayer?.isPlaying!! && !paused) {
            //mediaPlayer is playing
            actionIntentStartPause.action = ACTION_PAUSE
            actionPendingIntentStartPause = PendingIntent.getService(this, 0, actionIntentStartPause, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            //mediaPlayer is paused
            actionIntentStartPause.action = ACTION_START
            actionIntentStartPause.putExtra("uri", mediaPlayerState.uri.toString())
            actionIntentStartPause.putExtra("opis", mediaPlayerState.title)
            actionPendingIntentStartPause = PendingIntent.getService(this, 0, actionIntentStartPause, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val playPauseIcon = if (mediaPlayer?.isPlaying!! && !paused) R.drawable.pause else R.drawable.play

        val builder = NotificationCompat.Builder(this, channelID)
            .setContentTitle(mediaPlayerState.title)
            .setContentText(getConcatTime(mediaPlayerState.currentPosition, mediaPlayerState.duration))
            .setSmallIcon(R.mipmap.ic_lekcionar_logo_inverted_small)
            .setChannelId(channelID)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0)
            )
            .addAction(playPauseIcon, if (mediaPlayer?.isPlaying!! && !paused) "Pause" else "Play", actionPendingIntentStartPause)


        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentIntent(resultPendingIntent)
        builder.setDeleteIntent(actionPendingIntentExit)
        return builder.build()
    }

    fun foreground() {
        startForeground(NOTIFICATION_ID, createNotification())
    }

    fun background() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun getConcatTime(current: Int, duration: Int): String {
        val curr = getTime(current)
        val dur = getTime(duration)
        return "$curr / $dur"
    }

    private fun getTime(milliseconds: Int): String {
        val minutes = milliseconds / 1000 / 60
        val seconds = milliseconds / 1000 % 60
        if (seconds < 10) {
            return "$minutes:0$seconds"
        }
        return "$minutes:$seconds"
    }

}