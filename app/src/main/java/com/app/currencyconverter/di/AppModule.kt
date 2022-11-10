package com.app.currencyconverter.di


import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.app.currencyconverter.BuildConfig
import com.app.currencyconverter.datasource.remote.ParseErrors
import com.app.currencyconverter.datasource.remote.ApiKeyAuth
import com.app.currencyconverter.datasource.local.DigitifyDB
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    internal fun retrofit(gson: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client).build()
    }

    @Provides
    @Singleton
    internal fun okHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        keyAuth: ApiKeyAuth
    ): OkHttpClient {
        return OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(keyAuth)
            .addInterceptor(loggingInterceptor)
            .build()
    }


    @Provides
    @Singleton
    internal fun gson(): Gson {
        return GsonBuilder()
            //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .create()
    }

    @Provides
    @Singleton
    internal fun parseErrors(): ParseErrors {
        return ParseErrors()
    }

    @Provides
    @Singleton
    internal fun apiKeyAuth(): ApiKeyAuth {
        val apiKeyAuth = ApiKeyAuth("query", "app_id")
        apiKeyAuth.apiKey = BuildConfig.API_KEY
        return apiKeyAuth
    }

    @Provides
    @Singleton
    internal fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG)
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }


    @Provides
    @Singleton
    internal fun workManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    internal fun digitifyDb(@ApplicationContext context: Context, gson: Gson): DigitifyDB {
        return Room.databaseBuilder(context, DigitifyDB::class.java, "digitify_db")
            .build()
    }

}
