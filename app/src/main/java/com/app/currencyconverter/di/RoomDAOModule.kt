package com.app.currencyconverter.di

import com.app.currencyconverter.datasource.local.CurrencyConverterDB
import com.app.currencyconverter.datasource.local.dao.CurrencyRateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomDAOModule {
    @Provides
    @Singleton
    fun provideCurrencyRateDao(db: CurrencyConverterDB): CurrencyRateDao = db.currencyDao()
}