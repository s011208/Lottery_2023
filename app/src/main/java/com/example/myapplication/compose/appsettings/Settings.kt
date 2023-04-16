package com.example.myapplication.compose.appsettings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


private const val SETTINGS = "settings"
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)

const val SETTINGS_KEY_FONT_SIZE = "font_size"
const val SETTINGS_KEY_DAY_NIGHT_MODE = "day_night_mode"