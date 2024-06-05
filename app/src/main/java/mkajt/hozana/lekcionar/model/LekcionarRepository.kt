package mkajt.hozana.lekcionar.model

import android.content.Context
import android.util.Log

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mkajt.hozana.lekcionar.Constants
import mkajt.hozana.lekcionar.model.apiService.RetrofitManager
import mkajt.hozana.lekcionar.model.database.LekcionarDB
import mkajt.hozana.lekcionar.model.database.PodatkiEntity
import mkajt.hozana.lekcionar.model.database.RedEntity
import mkajt.hozana.lekcionar.model.database.SkofijaEntity
import mkajt.hozana.lekcionar.model.dto.LekcionarDTO
import mkajt.hozana.lekcionar.model.dto.MapDTO
import mkajt.hozana.lekcionar.model.dto.Mapper
import mkajt.hozana.lekcionar.model.dto.PodatkiDTO
import mkajt.hozana.lekcionar.model.dto.RedDTO
import mkajt.hozana.lekcionar.model.dto.SkofijaDTO
import okhttp3.Headers
import retrofit2.HttpException
import retrofit2.Response

class LekcionarRepository(
    mContext: Context,
    private val lekcionarDB: LekcionarDB,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    companion object {
        val TAG = LekcionarRepository::class.java.simpleName
    }

    private val lekcionarApi = RetrofitManager.lekcionarApi



    private var context: Context? = null

    init {
        context = mContext
    }

    suspend fun getLekcionarDataFromApi(): Long {
        val updatedTimestamp: Long
        try {
            val response: Response<LekcionarDTO> = withContext(ioDispatcher) {
                lekcionarApi?.getLekcionarData(Constants.BASE, Constants.KEY)!!
            }

            val header: Headers = response.headers()
            updatedTimestamp = if (header["timestamp"] != null) header["timestamp"]!!.toLong() else 0L

            if (response.isSuccessful) {
                val lekcionarDTO: LekcionarDTO? = response.body()
                if (lekcionarDTO != null) {
                    insertDataIntoDB(lekcionarDTO)
                    Log.d(TAG,"Data Loaded!")
                }
            } else {
                Log.e(TAG, "Failed to fetch data from API: ${response.message()}")
            }
            return updatedTimestamp
        } catch (e: HttpException) {
            Log.e(TAG, "Failed to fetch data from API: ${e.message()}")
            return 0L
        } catch (e: Exception) {
            Log.e(TAG, "An error occurred: ${e.message}")
            return 0L
        }
    }

    suspend fun getIdPodatekFromMap(selektor: String): String? {
        return withContext(ioDispatcher) {
            lekcionarDB.lekcionarDao().getIdPodatekFromMap(selektor)
        }
    }

    suspend fun getPodatki(idPodatek: List<String>): List<PodatkiEntity> {
        return withContext(ioDispatcher) {
            val podatki = mutableListOf<PodatkiEntity>()
            for (id in idPodatek) {
                val podatek = lekcionarDB.lekcionarDao().getPodatki(id)
                if (podatek != null) {
                    podatki.add(podatek)
                }
            }
            return@withContext podatki
        }
    }

    suspend fun countPodatki(): Int {
        return withContext(ioDispatcher) {
            lekcionarDB.lekcionarDao().countPodatki()
        }
    }

    suspend fun getSmallestTimestamp(): Long {
        return withContext(ioDispatcher) {
            lekcionarDB.lekcionarDao().getSmallestTimestamp()
        }
    }

    suspend fun getBiggestTimestamp(): Long {
        return withContext(ioDispatcher) {
            lekcionarDB.lekcionarDao().getBiggestTimestamp()
        }
    }

    suspend fun getRedList(): List<RedEntity> {
        return withContext(ioDispatcher) {
            lekcionarDB.lekcionarDao().getRedList()
        }
    }

    suspend fun getSkofijaList(): List<SkofijaEntity> {
        return withContext(ioDispatcher) {
            lekcionarDB.lekcionarDao().getSkofijaList()
        }
    }

    private suspend fun insertDataIntoDB(lekcionarDTO: LekcionarDTO) {
        withContext(ioDispatcher) {
            insertMapToDB(lekcionarDTO.map)
            insertPodatkiToDB(lekcionarDTO.data)
            insertRedToDB(lekcionarDTO.redovi)
            insertSkofijaToDB(lekcionarDTO.skofije)
        }
    }

    private suspend fun insertMapToDB(mapDTO: List<MapDTO>) {
        val mapEntities = Mapper.mapMapDtoToEntity(mapDTO)
        lekcionarDB.lekcionarDao().insertMap(mapEntities)
    }

    private suspend fun insertPodatkiToDB(podatkiDTO: List<PodatkiDTO>) {
        val podatkiEntities = Mapper.mapPodatkiDtoToEntity(podatkiDTO)
        lekcionarDB.lekcionarDao().insertPodatki(podatkiEntities)
    }

    private suspend fun insertRedToDB(redDTO: List<RedDTO>) {
        val redEntities = Mapper.mapRedDtoToEntity(redDTO)
        lekcionarDB.lekcionarDao().insertRed(redEntities)
    }

    private suspend fun insertSkofijaToDB(skofijaDTO: List<SkofijaDTO>) {
        val skofijaEntities = Mapper.mapSkofijaDtoToEntity(skofijaDTO)
        lekcionarDB.lekcionarDao().insertSkofija(skofijaEntities)
    }
}