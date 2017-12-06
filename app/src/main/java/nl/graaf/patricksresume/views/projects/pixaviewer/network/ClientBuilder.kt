package nl.graaf.patricksresume.views.projects.pixaviewer.network

import com.google.gson.GsonBuilder
import nl.graaf.patricksresume.views.projects.pixaviewer.network.deserializer.ImageResponseDeserializer
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * Created by patrick on 12/6/17.
 * 4:40 PM, Notive B.V.
 *
 * © Copyright 2017
 */
class ClientBuilder {
    companion object {
        private var mApiClient: IPixaAPI? = null

        fun getPixaAPIClient(): IPixaAPI {
            if (mApiClient == null) {
                val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                        .registerTypeAdapter(ImageResponse::class.java,
                                ImageResponseDeserializer()).create()

                val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .cipherSuites(
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                        .build()
                val okHttpClient = OkHttpClient.Builder()
                        .connectionSpecs(Collections.singletonList(spec))
                        .addInterceptor { chain ->
                            val originalRequest = chain.request()
                            val originalHttpUrl: HttpUrl = originalRequest.url()
                            val url: HttpUrl = originalHttpUrl.newBuilder()
                                    .addQueryParameter("key", IPixaAPI.API_KEY)
                                    .build()
                            val requestBuilder: Request.Builder = originalRequest.newBuilder().url(url)
                            chain.proceed(requestBuilder.build())
                        }.build()

                val retrofit: Retrofit = Retrofit.Builder().baseUrl(IPixaAPI.ENDPOINT)
                        .client(okHttpClient)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson)).build()
                mApiClient = retrofit.create(IPixaAPI::class.java)
            }

            return mApiClient!!
        }
    }
}