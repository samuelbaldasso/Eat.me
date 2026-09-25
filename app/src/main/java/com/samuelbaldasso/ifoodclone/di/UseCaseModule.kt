package com.samuelbaldasso.ifoodclone.di

import com.samuelbaldasso.ifoodclone.core.domain.usecase.CalculateDishPriceUseCase
import com.samuelbaldasso.ifoodclone.core.domain.usecase.ValidateDishSelectionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideValidateDishSelectionUseCase(): ValidateDishSelectionUseCase {
        return ValidateDishSelectionUseCase()
    }

    @Provides
    @Singleton
    fun provideCalculateDishPriceUseCase(): CalculateDishPriceUseCase {
        return CalculateDishPriceUseCase()
    }
}
