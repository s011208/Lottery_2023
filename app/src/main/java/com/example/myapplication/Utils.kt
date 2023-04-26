package com.example.myapplication

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.example.service.cache.DayNightMode

object Utils {

    fun setMode(context: Context, mode: DayNightMode) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            val localMode = when (mode) {
                DayNightMode.DAY -> AppCompatDelegate.MODE_NIGHT_NO
                DayNightMode.NIGHT -> AppCompatDelegate.MODE_NIGHT_YES
                DayNightMode.AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            AppCompatDelegate.setDefaultNightMode(localMode)
        } else {
            val localMode = when (mode) {
                DayNightMode.DAY -> UiModeManager.MODE_NIGHT_NO
                DayNightMode.NIGHT -> UiModeManager.MODE_NIGHT_YES
                DayNightMode.AUTO -> UiModeManager.MODE_NIGHT_AUTO
            }
            (context.applicationContext.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).setApplicationNightMode(
                localMode
            )
        }
    }
}

data class ImmutableListWrapper<T>(val wrapper: List<T>)