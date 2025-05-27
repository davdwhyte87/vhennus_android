package com.vhennus.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

import com.vhennus.BuildConfig
import com.vhennus.general.data.APIService
import com.vhennus.general.domain.GenericResp
import com.vhennus.general.domain.GenericRespAdapter
import com.vhennus.general.domain.GenericRespAdapterFactory
import com.vhennus.general.utils.BigDecimalSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.dnsoverhttps.DnsOverHttps
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.net.InetAddress
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    @Named("customGson")
    fun provideGson(): Gson {
        Log.d("Gson", "✅ Registering GenericRespAdapterFactory...")
        val gson = GsonBuilder()
            .serializeNulls()
            .setLenient()
            .registerTypeAdapterFactory(GenericRespAdapterFactory())
            .create()
        return gson
    }


    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(@Named("customGson") gson: Gson): Retrofit {
        val dohUrl = "https://dns.google/dns-query".toHttpUrl()
        val googleDoH = DnsOverHttps.Builder()
            .client(OkHttpClient())       // reuse if you have a shared client
            .url(dohUrl)                  // now using the extension-parsed URL
            .bootstrapDnsHosts(           // optional, to bypass system DNS
                InetAddress.getByName("8.8.8.8"),
                InetAddress.getByName("8.8.4.4")
            )
            .build()

        Log.d("Retrofit", "✅ Using custom Gson instance: $gson")
        val okHttpClient = OkHttpClient.Builder()
            .dns(googleDoH)
            .connectTimeout(120, TimeUnit.SECONDS)   // Set connection timeout
            .readTimeout(120, TimeUnit.SECONDS)      // Set read timeout
            .writeTimeout(120, TimeUnit.SECONDS)     // Set write timeout
            .addInterceptor { chain: Interceptor.Chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            coerceInputValues = true
        }

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL+"/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BlockchainRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BlockchainAPIService

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    @BlockchainRetrofit
    fun provideBlockchainRetrofit(@Named("customGson") gson: Gson): Retrofit {
        val dohUrl = "https://dns.google/dns-query".toHttpUrl()
        val googleDoH = DnsOverHttps.Builder()
            .client(OkHttpClient())       // reuse if you have a shared client
            .url(dohUrl)                  // now using the extension-parsed URL
            .bootstrapDnsHosts(           // optional, to bypass system DNS
                InetAddress.getByName("8.8.8.8"),
                InetAddress.getByName("8.8.4.4")
            )
            .build()

        Log.d("Retrofit", "✅ Using custom Gson instance: $gson")
        val okHttpClient = OkHttpClient.Builder()
            .dns(googleDoH)
            .connectTimeout(120, TimeUnit.SECONDS)   // Set connection timeout
            .readTimeout(120, TimeUnit.SECONDS)      // Set read timeout
            .writeTimeout(120, TimeUnit.SECONDS)     // Set write timeout
            .addInterceptor { chain: Interceptor.Chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            coerceInputValues = true
        }

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BLOCKCHAIN_URL+"/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }
    @Provides
    @Singleton
    @BlockchainAPIService
    fun provideBlockchainApiService(@BlockchainRetrofit retrofit:  Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideJson(): Json{
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            coerceInputValues = true
            serializersModule = SerializersModule {
                contextual(BigDecimal::class, BigDecimalSerializer)
            }
        }

        return json

    }
}

