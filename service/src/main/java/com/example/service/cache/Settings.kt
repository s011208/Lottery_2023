package com.example.myapplication.compose.appsettings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


private const val SETTINGS = "settings"
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)

const val SETTINGS_KEY_FONT_SIZE = "font_size"
const val SETTINGS_KEY_DAY_NIGHT_MODE = "day_night_mode"
const val SETTINGS_EXTRA_SPACING_LTO_TABLE = "extra_spacing_lto_table"
const val SETTINGS_EXTRA_SPACING_LTO_BIG_TABLE = "extra_spacing_lto_big_table"
const val SETTINGS_EXTRA_SPACING_LTO_HK_TABLE = "extra_spacing_lto_hk_table"
const val SETTINGS_EXTRA_SPACING_LTO_539_TABLE = "extra_spacing_lto_539_table"
const val SETTINGS_EXTRA_SPACING_LIST_TABLE = "extra_spacing_list_table"
const val SETTINGS_SHOW_DIVIDE_LINE = "show_divide_line"

const val SETTINGS_EXTRA_SPACING_PLUS_MINUS = "extra_spacing_p_m"
const val SETTINGS_FONT_SIZE_PLUS_MINUS = "font_size_p_m"
const val SETTINGS_SHOW_DIVIDE_LINE_PLUS_MINUS = "show_divide_line_p_m"