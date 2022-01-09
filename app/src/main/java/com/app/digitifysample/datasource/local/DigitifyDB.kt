package com.app.digitifysample.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.digitifysample.datasource.local.dao.CurrencyRateDao
import com.app.digitifysample.datasource.local.entity.CurrencyEntity
import com.app.digitifysample.datasource.local.entity.CurrencyRateEntity

@Database(
    entities = [CurrencyEntity::class, CurrencyRateEntity::class],
    version = 1
)
abstract class DigitifyDB : RoomDatabase() {
    abstract fun currencyDao(): CurrencyRateDao
}
