package si.hozana.lekcionar

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.Headers
import retrofit2.HttpException
import retrofit2.Response
import si.hozana.lekcionar.model.LekcionarRepository
import si.hozana.lekcionar.model.apiService.RetrofitManager
import si.hozana.lekcionar.model.dataStore.DataStoreManager
import si.hozana.lekcionar.model.database.LekcionarDB
import si.hozana.lekcionar.model.dto.LekcionarDTO
import si.hozana.lekcionar.model.dto.Mapper
import java.util.concurrent.TimeUnit

class DownloadWorker(context : Context, params : WorkerParameters) : CoroutineWorker(context, params) {

    private val dataStore = DataStoreManager(context)
    private val lekcionarApi = RetrofitManager.lekcionarApi
    private val lekcionarDB = LekcionarDB.getInstance(context)

    companion object {
        val TAG = "DownloadWorker"
        fun createWorkRequest() = PeriodicWorkRequestBuilder<DownloadWorker>(1, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build())
            .build()
    }

    override suspend fun doWork(): Result {
        try {
            val dataStoreUpdateTimestamp = dataStore.getUpdatedDataTimestamp().first()
            Log.d(TAG, "dataStore: $dataStoreUpdateTimestamp")
            val response: Response<Void> = withContext(Dispatchers.IO) {
                lekcionarApi?.getHeadLekcionarData(Constants.BASE, Constants.KEY)!!
            }
            val header: Headers = response.headers()
            val updatedTimestamp = if (header["timestamp"] != null) header["timestamp"]!!.toLong() else 0L
            Log.d(TAG, "update: $updatedTimestamp")
            if (updatedTimestamp > dataStoreUpdateTimestamp) {
                Log.d(TAG, "bigger")
                val response2: Response<LekcionarDTO> = withContext(Dispatchers.IO) {
                    lekcionarApi?.getLekcionarData(Constants.BASE, Constants.KEY)!!
                }
                if (response2.isSuccessful) {
                    val lekcionarDTO: LekcionarDTO? = response2.body()
                    if (lekcionarDTO != null) {
                        lekcionarDB.lekcionarDao().deleteAllFromMap()
                        lekcionarDB.lekcionarDao().deleteAllFromPodatki()
                        lekcionarDB.lekcionarDao().deleteAllFromRed()
                        lekcionarDB.lekcionarDao().deleteAllFromSkofija()

                        val mapEntities = Mapper.mapMapDtoToEntity(lekcionarDTO.map)
                        val podatkiEntities = Mapper.mapPodatkiDtoToEntity(lekcionarDTO.data)
                        val redEntities = Mapper.mapRedDtoToEntity(lekcionarDTO.redovi)
                        val skofijaEntities = Mapper.mapSkofijaDtoToEntity(lekcionarDTO.skofije)
                        lekcionarDB.lekcionarDao().insertMap(mapEntities)
                        lekcionarDB.lekcionarDao().insertPodatki(podatkiEntities)
                        lekcionarDB.lekcionarDao().insertRed(redEntities)
                        lekcionarDB.lekcionarDao().insertSkofija(skofijaEntities)

                        dataStore.setUpdatedDataTimestamp(updatedTimestamp)
                    }
                }
            }
            //dataStore.setTestUpdateTimestamp(System.currentTimeMillis())
        } catch (e: HttpException) {
            Log.e(LekcionarRepository.TAG, "Failed to fetch data from API: ${e.message()}")
            return Result.failure()
        } catch (e: Exception) {
            Log.e(LekcionarRepository.TAG, "An error occurred: ${e.message}")
            return Result.failure()
        }
        return Result.success()
    }

}