package com.rittmann.common.repositories.postecode

import retrofit2.http.GET
import retrofit2.http.Url

interface PostalCodeApi {

    @GET
    suspend fun downloadAllPostalCode(
        @Url url: String = "https://raw.githubusercontent.com/centraldedados/codigos_postais/master/data/codigos_postais.csv"
    )
}