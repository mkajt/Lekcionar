package mkajt.hozana.lekcionar.model.apiService

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import mkajt.hozana.lekcionar.Constants
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitManager {
    val lekcionarApi: LekcionarApi

    init {
        val contentType = "application/json".toMediaType()

        val client = OkHttpClient.Builder()
            .addInterceptor(CustomInterceptor())
            .build()

        lekcionarApi = Retrofit.Builder()
            .baseUrl(Constants.ENDPOINT_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(client)
            .build()
            .create(LekcionarApi::class.java)
    }

}