package com.rittmann.datasource.network.di

import android.app.Application
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rittmann.datasource.network.BaseRestApi
import com.rittmann.datasource.repositories.postalcode.PostalCodeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class PostalCodeApiNetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, application: Application): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://pokeapi.co/api/v2/") // TODO change it
            .client(provideOkhttpClient(application))
            .build()
    }

    @Provides
    @Singleton
    fun providePokeListApi(
        retrofit: Retrofit
    ): PostalCodeApi {
        return retrofit.create(PostalCodeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(application: Application): OkHttpClient {
        return BaseRestApi.getOkHttpClient(application)
    }
}