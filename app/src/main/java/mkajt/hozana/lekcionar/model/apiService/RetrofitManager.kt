package mkajt.hozana.lekcionar.model.apiService

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import mkajt.hozana.lekcionar.Constants
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create

private var json = Json {
    isLenient = true
    ignoreUnknownKeys = true
}


object RetrofitManager {
    var retrofitService: Retrofit? = null
    var lekcionarApi: LekcionarApi? = null

    init {
        val contentType = "application/json".toMediaType()

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        retrofitService = Retrofit.Builder()
            .baseUrl(Constants.ENDPOINT_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(client)
            .build()

        lekcionarApi = retrofitService!!.create(LekcionarApi::class.java)
    }
}