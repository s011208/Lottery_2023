package com.example.myapplication.compose.appsettings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


private const val SETTINGS = "settings"
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)

const val SETTINGS_KEY_FONT_SIZE = "font_size"
const val SETTINGS_KEY_DAY_NIGHT_MODE = "day_night_mode"
const val SETTINGS_EXTRA_SPACING_NORMAL_TABLE = "extra_spacing_normal_table"
const val SETTINGS_EXTRA_SPACING_LIST_TABLE = "extra_spacing_list_table"
const val SETTINGS_SHOW_DIVIDE_LINE = "show_divide_line"