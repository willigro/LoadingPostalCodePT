package com.rittmann.common.repositories

import java.io.File
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request

interface PostalCodeRepository {
    fun download(
        url: String = "https://raw.githubusercontent.com/centraldedados/codigos_postais/master/data/codigos_postais.csv",
        file: File
    ): Flow<Long>
}

class PostalCodeRepositoryImpl @Inject constructor(
) : PostalCodeRepository {

    private lateinit var okHttpClient: OkHttpClient

    override fun download(url: String, file: File) = flow {
        val okHttpBuilder = OkHttpClient.Builder()
            .connectTimeout(1L, TimeUnit.MINUTES)
            .readTimeout(1L, TimeUnit.MINUTES)

        okHttpClient = okHttpBuilder.build()

        val request =
            Request.Builder().addHeader("Accept-Encoding", "gzip, deflate, br").url(url).build()

        val response = okHttpClient.newCall(request).execute()

        val body = response.body
        val responseCode = response.code

        if (responseCode >= HttpURLConnection.HTTP_OK &&
            responseCode < HttpURLConnection.HTTP_MULT_CHOICE &&
            body != null
        ) {
            body.byteStream().apply {
                val length = body.contentLength()

                file.outputStream().use { fileOut ->
                    var bytesCopied = 0
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytes = read(buffer)

                    while (bytes >= 0) {
                        fileOut.write(buffer, 0, bytes)
                        bytesCopied += bytes
                        bytes = read(buffer)
                        // TODO change the return to something like Pair
                        emit(((bytesCopied * 100) / length)) // IN PROGRESS - 1, 2, 60, 99, 100...
                    }
                }
                emit(100) // DONE - 100
            }
        } else {
            // Report the error
        }
    }


}