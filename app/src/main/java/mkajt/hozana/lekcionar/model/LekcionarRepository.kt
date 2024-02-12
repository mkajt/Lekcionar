package mkajt.hozana.lekcionar.model

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import mkajt.hozana.lekcionar.Constants
import mkajt.hozana.lekcionar.model.apiService.RetrofitManager
import mkajt.hozana.lekcionar.model.dto.LekcionarDTO
import mkajt.hozana.lekcionar.model.dto.MapDTO
import mkajt.hozana.lekcionar.model.dto.PodatkiDTO
import mkajt.hozana.lekcionar.model.dto.RedDTO
import mkajt.hozana.lekcionar.model.dto.SkofijaDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LekcionarRepository(application: Application?) {
    companion object {
        val TAG = LekcionarRepository::class.java.simpleName
    }

    private val lekcionarApi = RetrofitManager.lekcionarApi

    var lekcionarData: LekcionarDTO? = null
    var redovi: List<RedDTO>? = null
    var skofije: List<SkofijaDTO>? = null
    var map: List<MapDTO>? = null
    var podatki: List<PodatkiDTO>? = null
    private var context: Context? = null

    //TODO do I use LiveData (not MutableLiveData, because no changes will be applied) or not?
    /*suspend fun getLekcionarData() {
        try {
            val response = lekcionarApi.getLekcionarData(Constants.BASE, Constants.KEY)
            Log.d(TAG, "$response")
            if (response.isSuccessful) {
                Log.d(TAG, "SUCCESS")
                Log.d(TAG, "${response.body()}")
                lekcionarData.postValue(response.body())
            } else {
                Log.d(TAG, "FAILURE")
                Log.d(TAG, "${response.body()}")
            }
        } catch (e: UnknownHostException) {
            //when there is no internet connection or host is unavailable
            e.message?.let { Log.e(TAG, it) }
        } catch (e: SocketTimeoutException) {
            //when timeout
            e.message?.let { Log.e(TAG, it) }
        } catch (e: Exception) {
            //generic handling
            e.message?.let { Log.e(TAG, it) }
        }
    }*/

    init {
        context = application?.applicationContext
    }

    suspend fun getLekcionarData() {
        lekcionarApi?.getLekcionarData(Constants.BASE, Constants.KEY)?.enqueue(object: Callback<LekcionarDTO?> {
            override fun onResponse(call: Call<LekcionarDTO?>, response: Response<LekcionarDTO?>) {
                redovi = response.body()?.redovi
                skofije = response.body()?.skofije
                map = response.body()?.map
                podatki = response.body()?.data
                Toast.makeText(context, "Loading.", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<LekcionarDTO?>, t: Throwable) {
                Toast.makeText(context, "Cannot connect to API. No internet connection.", Toast.LENGTH_SHORT).show()
            }

        })
    }

}