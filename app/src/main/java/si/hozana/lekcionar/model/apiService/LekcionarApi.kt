package si.hozana.lekcionar.model.apiService

import si.hozana.lekcionar.model.dto.LekcionarDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface LekcionarApi {

    @GET("api.php")
    suspend fun getLekcionarData(@Query("kaj") base: String,
                                 @Query("kljuc") key: String): Response<LekcionarDTO>

    @Headers("Accept: null")
    @GET("api.php")
    suspend fun getHeadLekcionarData(@Query("kaj") base: String,
                                     @Query("kljuc") key: String): Response<Void>

}