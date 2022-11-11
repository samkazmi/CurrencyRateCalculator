package com.app.currencyconverter.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.currencyconverter.datasource.local.dao.CurrencyRateDao
import com.app.currencyconverter.datasource.local.entity.CurrencyEntity
import com.app.currencyconverter.datasource.local.entity.CurrencyRateEntity

@Database(
    entities = [CurrencyEntity::class, CurrencyRateEntity::class],
    version = 1
)
abstract class CurrencyConverterDB : RoomDatabase() {
    abstract fun currencyDao(): CurrencyRateDao
}
