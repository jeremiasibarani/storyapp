package com.example.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.datastore.AuthPreferences
import com.example.storyapp.model.local.StoryDatabase
import com.example.storyapp.model.network.ApiConfig
import com.example.storyapp.repository.AuthRepository
import com.example.storyapp.repository.StoryRepository

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "authentication")

object Injector {
    fun provideAuthRepository(context : Context) : AuthRepository{
        val authApiService = ApiConfig.getAuthApiService()
        val authPreferences = AuthPreferences.getInstance(context.dataStore)
        return AuthRepository(authApiService, authPreferences)
    }

    fun provideStoryRepository(context: Context) : StoryRepository{
        val storyApiService = ApiConfig.getStoryApiService()
        val authPreferences = AuthPreferences.getInstance(context.dataStore)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository(storyApiService, authPreferences, storyDatabase)
    }
}