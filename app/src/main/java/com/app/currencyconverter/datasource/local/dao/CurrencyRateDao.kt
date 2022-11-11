package com.app.currencyconverter.datasource.local.dao

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.currencyconverter.datasource.local.entity.CurrencyEntity
import com.app.currencyconverter.datasource.local.entity.CurrencyRateEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.annotations.TestOnly

@Dao
interface CurrencyRateDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllRates(list: List<CurrencyRateEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllCurrencies(list: List<CurrencyEntity>)

    @Query("SELECT * FROM currencyrateentity")
    fun getAllRates(): Flow<List<CurrencyRateEntity>>

    @VisibleForTesting
    @Query("SELECT * FROM currencyrateentity")
    fun getAllRatesList(): List<CurrencyRateEntity>

    @Query("SELECT * FROM currencyrateentity")
    fun getAllRatesPaged(): PagingSource<Int, CurrencyRateEntity>

    @Query("SELECT * FROM currencyentity")
    fun getCurrencyListLive(): LiveData<List<CurrencyEntity>>

    @Query("SELECT COUNT(currencyCode) FROM currencyentity")
    suspend fun getCurrencyCount(): Int

    @Query("SELECT * FROM currencyrateentity where code = :currencyCode")
    suspend fun getCurrencyRate(currencyCode: String): CurrencyRateEntity?

    @Query("SELECT COUNT(code) FROM currencyrateentity")
    suspend fun getRatesCount(): Int


}
