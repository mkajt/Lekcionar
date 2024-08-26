package si.kapucini.lekcionar.model.apiService

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import si.kapucini.lekcionar.Constants
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

private var json = Json {
    isLenient = true
    ignoreUnknownKeys = true
}

object RetrofitManager {
    var retrofitService: Retrofit? = null
    var lekcionarApi: LekcionarApi? = null

    init {
        val contentType = "application/json".toMediaType()

        val client = OkHttpClient.Builder()
            .build()

        retrofitService = Retrofit.Builder()
            .baseUrl(Constants.ENDPOINT_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(client)
            .build()

        lekcionarApi = retrofitService!!.create(LekcionarApi::class.java)
    }
}