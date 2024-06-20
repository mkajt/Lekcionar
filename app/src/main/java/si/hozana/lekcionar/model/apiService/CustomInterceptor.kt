package si.hozana.lekcionar.model.apiService

import si.hozana.lekcionar.Constants
import okhttp3.Interceptor
import okhttp3.Response

class CustomInterceptor: Interceptor {
    //TODO !not in use! - delete when finished with API, until let it be in case of...
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.newBuilder()
            .addQueryParameter("kaj", Constants.BASE)
            .addQueryParameter("kljuc", Constants.KEY)
            .build()
        val request = chain.request().newBuilder()
            .url(url)
            .build()
        return chain.proceed(request)
    }
}