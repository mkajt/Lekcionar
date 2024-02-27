package mkajt.hozana.lekcionar.model

import android.content.Context
import android.util.Log

import android.widget.Toast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mkajt.hozana.lekcionar.Constants
import mkajt.hozana.lekcionar.model.apiService.RetrofitManager
import mkajt.hozana.lekcionar.model.database.LekcionarDB
import mkajt.hozana.lekcionar.model.database.PodatkiEntity
import mkajt.hozana.lekcionar.model.dto.LekcionarDTO
import mkajt.hozana.lekcionar.model.dto.MapDTO
import mkajt.hozana.lekcionar.model.dto.Mapper
import mkajt.hozana.lekcionar.model.dto.PodatkiDTO
import mkajt.hozana.lekcionar.model.dto.RedDTO
import mkajt.hozana.lekcionar.model.dto.SkofijaDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LekcionarRepository(
    mContext: Context,
    private val lekcionarDB: LekcionarDB,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    companion object {
        val TAG = LekcionarRepository::class.java.simpleName
    }

    private val lekcionarApi = RetrofitManager.lekcionarApi

    //var redoviDTO: List<RedDTO>? = null
    //var skofijeDTO: List<SkofijaDTO>? = null
    //var mapDTO: List<MapDTO>? = null
    //var podatkiDTO: List<PodatkiDTO>? = null

    private var context: Context? = null

    init {
        context = mContext
    }

    suspend fun getLekcionarDataFromApi() {
        try {
            val lekcionarDTO = withContext(ioDispatcher) {
                lekcionarApi?.getLekcionarData(Constants.BASE, Constants.KEY)
            }
            insertDataIntoDB(lekcionarDTO!!)
            Log.d(TAG,"Data Loaded!")
        } catch (e: HttpException) {
            Log.d(TAG, "Failed to fetch data from API: ${e.message()}")
        } catch (e: Exception) {
            Log.d(TAG, "An error occurred: ${e.message}")
        } finally {
            Log.d(TAG, "Successfully loaded data from API and inserted into DB")
        }
    }

    suspend fun getIdPodatekFromMap(selektor: String): String {
        return withContext(ioDispatcher) {
            lekcionarDB.lekcionarDao().getIdPodatekFromMap(selektor)
        }
    }

    suspend fun getPodatki(id_podatek: String): PodatkiEntity {
        return withContext(ioDispatcher) {
            lekcionarDB.lekcionarDao().getPodatki(id_podatek)
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