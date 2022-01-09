package com.app.digitifysample.di.home

import androidx.work.WorkManager
import com.app.digitifysample.datasource.repository.CurrencyRepository
import com.app.digitifysample.datasource.usecase.CurrencyConversionUsecase
import com.app.digitifysample.datasource.usecase.CurrencyConversionUsecaseImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Suppress("unused")
@Module
@InstallIn(ViewModelComponent::class)
class HomeModule {

    @Provides
    @ViewModelScoped
    fun provideCurrencyConversionUsecase(
        currencyRepository: CurrencyRepository,
        workManager: WorkManager
    ): CurrencyConversionUsecase = CurrencyConversionUsecaseImp(currencyRepository, workManager)


}