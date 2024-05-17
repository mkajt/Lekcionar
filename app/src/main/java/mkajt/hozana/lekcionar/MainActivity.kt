package mkajt.hozana.lekcionar

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import mkajt.hozana.lekcionar.mediaPlayer.MediaPlayerService
import mkajt.hozana.lekcionar.model.LekcionarRepository
import mkajt.hozana.lekcionar.model.database.LekcionarDB
import mkajt.hozana.lekcionar.ui.routes.AppNavigation
import mkajt.hozana.lekcionar.ui.theme.AppTheme
import mkajt.hozana.lekcionar.viewModel.LekcionarViewModel
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity(), ActivityListener {

    companion object {
        val TAG = MainActivity::class.simpleName
    }

    private lateinit var lekcionarViewModel: LekcionarViewModel
    private lateinit var lekcionarRepository: LekcionarRepository
    private lateinit var lekcionarDB: LekcionarDB
    private lateinit var context: Context

    private val REQUEST_PERMISSION_POST_NOTIFICATIONS = 1
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val PERMISSION_POST_NOTIFICATIONS = arrayOf(Manifest.permission.POST_NOTIFICATIONS)

    private var mediaPlayerService: MediaPlayerService? = null
    private var serviceBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = application.applicationContext
        val localActivity = staticCompositionLocalOf<ActivityListener> {
            error("No ActivityListener provided")
        }

        lekcionarDB = LekcionarDB.getInstance(context)
        lekcionarRepository = LekcionarRepository(context, lekcionarDB, Dispatchers.IO)
        lekcionarViewModel = LekcionarViewModel(application, lekcionarRepository)
        lekcionarViewModel.checkDbAndFetchDataFromApi()

        setContent {
            CompositionLocalProvider(localActivity provides this@MainActivity) {
                AppTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        color = AppTheme.colorScheme.background
                    ) {
                        AppNavigation(viewModel = lekcionarViewModel, activityListener = this@MainActivity)
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_POST_NOTIFICATIONS) {
            if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        showPermissionDeniedDialog(Manifest.permission.POST_NOTIFICATIONS, REQUEST_PERMISSION_POST_NOTIFICATIONS)
                    }
                } else {
                    openAppSettings(context)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "Starting and binding service");

        val i = Intent(applicationContext, MediaPlayerService::class.java)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i)
        } else {
            startService(i)
        }*/

        startService(i)
        bindService(i, mConnection, 0);
    }

    override fun onResume() {
        super.onResume()
        //in case of error: kotlin.uninitializedpropertyaccessexception: lateinit property
        //mediaPlayerService?.injectViewModel(lekcionarViewModel)ž
    }

    override fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            //check if notification permission has been granted -> if not, ask again
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                    showPermissionDeniedDialog(Manifest.permission.POST_NOTIFICATIONS, REQUEST_PERMISSION_POST_NOTIFICATIONS)
                } else {
                    // Permission is not granted, request the permission
                    ActivityCompat.requestPermissions(this@MainActivity, PERMISSION_POST_NOTIFICATIONS, REQUEST_PERMISSION_POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun showPermissionDeniedDialog(permissions: String, permissionRequestCode: Int) {
        AlertDialog.Builder(this).apply {
            setCancelable(true)
            setMessage("Za predvajanje posnetka je potrebno dovoljenje za prikaz obvestil.")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permissions), permissionRequestCode)
            }
        }.show()
    }

    private fun openAppSettings(context: Context) {
        AlertDialog.Builder(this).apply {
            setCancelable(true)
            setMessage("Omogočite prikazovanje obvestil aplikacije v nastavitvah telefona.")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", context.packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        }.show()
    }

    override fun onStop() {
        super.onStop()

        if (serviceBound) {
            if (mediaPlayerService?.mediaPlayerState!!.isPlaying) {
                mediaPlayerService?.foreground()
                unbindService(mConnection)
                serviceBound = false
            } else {
                mediaPlayerService?.exit()

                //val i = Intent(this, MediaPlayerService::class.java)
                //i.action = MediaPlayerService.ACTION_EXIT
                //startService(i)

                //or:
                //mediaPlayerService?.exit()
                //stopService(Intent(this, MediaPlayerService::class.java))

                //mediaPlayerService?.background()
                //mediaPlayerService?.stopSelf()
                //unbindService(mConnection)
                //serviceBound = false
                //finishAndRemoveTask()
                //exitProcess(0)
                //exitClick()
            }
        }
    }

    override fun onDestroy() {
        //mediaPlayerService?.background()
        //mediaPlayerService?.exit()
        super.onDestroy()
    }

    // define a ServiceConnection
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Log.d(TAG, "Service bound")
            val binder = iBinder as MediaPlayerService.RunServiceBinder
            mediaPlayerService = binder.service

            mediaPlayerService?.background()

            serviceBound = true
            mediaPlayerService?.injectViewModel(lekcionarViewModel)
            mediaPlayerService?.mediaPlayerState?.let { lekcionarViewModel.updateMediaPlayerState(it) }

        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(TAG, "Service disconnect")
            serviceBound = false
        }
    }

    override fun playClick(uri: String, opis: String) {
        if (serviceBound) {
            Log.d(TAG, "Started playing")
            mediaPlayerService?.playInit(Uri.parse(uri), opis)
        } else {
            Log.d(TAG, "Binding service");

            val i = Intent(this, MediaPlayerService::class.java)
            i.action = MediaPlayerService.ACTION_START
            i.putExtra("uri", uri)
            i.putExtra("opis", opis)

            startService(i)

            bindService(i, mConnection, 0);
        }
    }

    override fun stopClick() {
        if (serviceBound) {
            Log.d(TAG, "Stopped playing")
            mediaPlayerService?.stop()
        }
    }

    override fun pauseClick() {
        if (serviceBound) {
            Log.d(TAG, "Paused playing")
            mediaPlayerService?.pause()
        }
    }

    override fun seekClick(position: Float) {
        if (serviceBound) {
            mediaPlayerService?.seek(position)
        }
    }

    // todo why use this -> onStop()
    override fun exitClick() {
        Log.d(TAG, "exitClick()")

        //if (mediaPlayerService != null) {
            if (serviceBound) {
                Log.d(TAG, "exitClick() service bound")
                mediaPlayerService?.background()
                mediaPlayerService?.exit()
                //stopService(Intent(this, MediaPlayerService::class.java))
                unbindService(mConnection)
                serviceBound = false
                //mediaPlayerService?.stopSelf()

            } /*else {
                Log.d(TAG, "exitClick() service unbound")
                mediaPlayerService?.exit()
                mediaPlayerService?.stopSelf()
                finishAndRemoveTask()
                exitProcess(0)
            }*/
        //}
        //finishAndRemoveTask()
        //exitProcess(0)
    }

    override fun getActivity(): Activity {
        return this
    }
}

interface ActivityListener {
    fun playClick(uri: String, opis: String)
    fun stopClick()
    fun pauseClick()
    fun seekClick(position: Float)
    fun exitClick()
    fun getActivity(): Activity
    fun requestNotificationPermission()
}