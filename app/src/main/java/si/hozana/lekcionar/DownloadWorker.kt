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
        fun createWorkRequest() = PeriodicWorkRequestBuilder<DownloadWorker>(1, TimeUnit.DAYS)
            .setConstraints(Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build())
            .setInitialDelay(1, TimeUnit.DAYS)
            .build()
    }

    override suspend fun doWork(): Result {
        try {
            val dataStoreUpdateTimestamp = dataStore.getUpdatedDataTimestamp().first()
            val response: Response<Void> = withContext(Dispatchers.IO) {
                lekcionarApi?.getHeadLekcionarData(Constants.BASE, Constants.KEY)!!
            }
            val header: Headers = response.headers()
            val updatedTimestamp = if (header["timestamp"] != null) header["timestamp"]!!.toLong() else 0L

            if (updatedTimestamp > dataStoreUpdateTimestamp) {

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

                        val firstDataTimestamp = lekcionarDB.lekcionarDao().getFirstDataTimestamp()
                        val lastDataTimestamp = lekcionarDB.lekcionarDao().getLastDataTimestamp()
                        dataStore.setUpdatedDataTimestamp(updatedTimestamp)
                        dataStore.setFirstDataTimestamp(firstDataTimestamp)
                        dataStore.setLastDataTimestamp(lastDataTimestamp)
                    }
                }
            }
            val current = System.currentTimeMillis()
            dataStore.setTestUpdateTimestamp(current) //TODO delete
        } catch (e: HttpException) {
            Log.e(TAG, "Failed to fetch data from API: ${e.message()}")
            return Result.failure()
        } catch (e: Exception) {
            Log.e(TAG, "An error occurred: ${e.message}")
            return Result.failure()
        }
        return Result.success()
    }

}