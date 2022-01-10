package com.app.digitifysample.di

import com.app.digitifysample.datasource.local.DigitifyDB
import com.app.digitifysample.datasource.local.dao.CurrencyRateDao
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
    fun provideCurrencyRateDao(db: DigitifyDB): CurrencyRateDao = db.currencyDao()
}