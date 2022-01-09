package com.app.digitifysample.datasource.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyEntity(
    @PrimaryKey val currencyCode: String,
    @NonNull val displayName: String
)