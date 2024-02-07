package mkajt.hozana.lekcionar.model.apiService

import mkajt.hozana.lekcionar.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class ApiClient {
    //TODO !not in use! delete when API works, let it be just in case...
    companion object {
        fun <T> create(service: Class<T>): T {
            val client = OkHttpClient.Builder()
                .addInterceptor(CustomInterceptor())
                .build()

            return Retrofit.Builder()
                .baseUrl(Constants.ENDPOINT_URL)
                .client(client)
                .build()
                .create(service)
        }
    }
}