package com.example.musify.di

import com.example.musify.viewmodels.homefeedviewmodel.greetingphrasegenerator.CurrentTimeBasedGreetingPhraseGenerator
import com.example.musify.viewmodels.homefeedviewmodel.greetingphrasegenerator.GreetingPhraseGenerator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class PhraseGeneratorModule {
    @Binds
    abstract fun bindCurrentTimeBasedGreetingPhraseGenerator(impl: CurrentTimeBasedGreetingPhraseGenerator): GreetingPhraseGenerator
}