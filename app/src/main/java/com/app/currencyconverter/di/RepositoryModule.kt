package com.app.currencyconverter.di

import com.app.currencyconverter.datasource.remote.ParseErrors
import com.app.currencyconverter.datasource.remote.apis.CurrencyApi
import com.app.currencyconverter.datasource.local.dao.CurrencyRateDao
import com.app.currencyconverter.datasource.repository.CurrencyRepository
import com.app.currencyconverter.datasource.repository.CurrencyRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideCurrencyApi(retrofit: Retrofit): CurrencyApi = retrofit.create(CurrencyApi::class.java)

    @Provides
    @Singleton
    fun provideCurrencyRepository(
        currencyApi: CurrencyApi,
        currencyRateDao: CurrencyRateDao,
        parseErrors: ParseErrors
    ): CurrencyRepository =
        CurrencyRepositoryImp(currencyApi, currencyRateDao, parseErrors)
}

// Here we provide a common repository that has @AppScope and used by multiple modules(by modules I mean Activity)