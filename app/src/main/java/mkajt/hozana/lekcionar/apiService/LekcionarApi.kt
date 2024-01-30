package mkajt.hozana.lekcionar.apiService

import retrofit2.http.GET

interface LekcionarApi {

    @GET("api.php?kaj=baza_lekcionar&kljuc=MtiOL9DKKBh59C0hIJYMcKOAQrZ")
    suspend fun getLekcionarData()

}