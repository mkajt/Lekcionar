package mkajt.hozana.lekcionar.model.apiService

import mkajt.hozana.lekcionar.model.dto.LekcionarDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LekcionarApi {

    //TODO ali naj vrne Response<LekcionarDTO> ali samo LekcionarDTO?
    @GET("api.php")
    suspend fun getLekcionarData(@Query("kaj") base: String,
                                 @Query("kljuc") key: String): Response<LekcionarDTO>

}