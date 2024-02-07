package mkajt.hozana.lekcionar.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import mkajt.hozana.lekcionar.Constants
import mkajt.hozana.lekcionar.model.apiService.RetrofitManager
import mkajt.hozana.lekcionar.model.dto.LekcionarDTO
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LekcionarRepository {
    companion object {
        val TAG = LekcionarRepository::class.java.simpleName
    }

    private val lekcionarApi = RetrofitManager.lekcionarApi

    val lekcionarData = MutableLiveData<LekcionarDTO>()

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

    suspend fun getLekcionarData(): LekcionarDTO? {
        return lekcionarApi?.getLekcionarData(Constants.BASE, Constants.KEY)
    }

}