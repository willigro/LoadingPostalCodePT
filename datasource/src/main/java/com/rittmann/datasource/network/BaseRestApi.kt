package com.rittmann.datasource.network

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import okhttp3.logging.HttpLoggingInterceptor


object BaseRestApi {
    const val CACHE_SIZE: Long = 5 * 1024 * 1024 // 5MB

    fun getOkHttpClient(context: Context): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { hostname, session -> true }

            val requestTimeout = 1L
            val myCache = Cache(context.cacheDir, CACHE_SIZE)

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            builder
                .cache(myCache)
                .readTimeout(requestTimeout, TimeUnit.MINUTES)
                .writeTimeout(requestTimeout, TimeUnit.MINUTES)
                .connectTimeout(requestTimeout, TimeUnit.MINUTES)

            return builder
                .addInterceptor(interceptor)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}