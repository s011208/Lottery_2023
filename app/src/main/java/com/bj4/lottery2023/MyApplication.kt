package com.bj4.lottery2023

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.room.Room
import androidx.work.Configuration
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bj4.lottery2023.compose.lotterylog.vm.LotteryLogViewModel
import com.bj4.lottery2023.compose.lotterytable.vm.LotteryTableViewModel
import com.bj4.lottery2023.compose.lotterytable.vm.Source
import com.bj4.lottery2023.compose.plusminus.vm.PlusMinusViewModel
import com.bj4.lottery2023.compose.possibility.vm.PossibilityScreenViewModel
import com.example.analytics.Analytics
import com.example.myapplication.compose.appsettings.SETTINGS_KEY_DAY_NIGHT_MODE
import com.example.myapplication.compose.appsettings.settingsDataStore
import com.example.service.cache.DayNightMode
import com.example.service.cache.Preferences
import com.example.service.cache.log.LotteryLogDatabase
import com.example.service.cache.lto.LotteryDataDatabase
import com.example.service.service.ParseService
import com.example.service.usecase.DisplayUseCase
import com.example.service.usecase.LotteryLogUseCase
import com.example.service.usecase.SettingsUseCase
import com.example.service.usecase.SyncUseCase
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import timber.log.Timber
import java.util.concurrent.TimeUnit


class MyApplication : Application(), Configuration.Provider {

    companion object {
        private const val LOG_TAG = "QQQQ"

        private const val REMOTE_CONFIG_FETCHING_INTERVAL = 3600L
        private const val REMOTE_CONFIG_SYNC_INTERVAL = "periodic_sync_duration"
    }

    override fun onCreate() {
        super.onCreate()

        initTimber()

        startKoin {
            // declare used Android context
            androidContext(this@MyApplication)
            // declare modules
            modules(myModule)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val dayNightSettings = settingsDataStore.data.stateIn(this).value[stringPreferencesKey(
                SETTINGS_KEY_DAY_NIGHT_MODE
            )]
            if (dayNightSettings != null) {
                Utils.setMode(this@MyApplication, DayNightMode.valueOf(dayNightSettings))
            } else {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    (getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).setApplicationNightMode(
                        UiModeManager.MODE_NIGHT_NO
                    )
                }
            }
        }

        initFirebase()
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        fetchRemoveConfigAndRunTask()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(priority, "$tag:$LOG_TAG", message, t)
                }
            })
        }
    }

    private fun scheduleSyncTask(context: Context, interval: Long) {
        val syncTask = PeriodicWorkRequestBuilder<SyncWorker>(
            interval, TimeUnit.HOURS, // repeatInterval (the period cycle)
            30, TimeUnit.MINUTES
        )
            .setInputData(Data.Builder().putString(SyncWorker.SOURCE, Source.PERIODIC.name).build())
            .build()


        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "Sync lottery periodic task",
                ExistingPeriodicWorkPolicy.UPDATE,
                syncTask
            )
    }

    private fun fetchRemoveConfigAndRunTask() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings: FirebaseRemoteConfigSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = REMOTE_CONFIG_FETCHING_INTERVAL
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf(REMOTE_CONFIG_SYNC_INTERVAL to 3))

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i(
                    "$REMOTE_CONFIG_SYNC_INTERVAL: ${
                        remoteConfig.getLong(
                            REMOTE_CONFIG_SYNC_INTERVAL
                        )
                    }"
                )
                scheduleSyncTask(this, remoteConfig.getLong(REMOTE_CONFIG_SYNC_INTERVAL))
            } else {
                Timber.w("Failed to fetch remote config")
            }
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.VERBOSE)
            .build()
    }
}

val myModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            LotteryDataDatabase::class.java, "lottery"
        ).fallbackToDestructiveMigration().build()
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            LotteryLogDatabase::class.java, "lottery-log"
        ).fallbackToDestructiveMigration().build()
    }

    single { LotteryTableViewModel(get(), get(), get(), get()) }
    single { ParseService(get()) }
    single { SyncUseCase(get()) }
    single { DisplayUseCase() }
    single { Preferences(get()) }
    single { SettingsUseCase(get()) }
    single { Analytics() }
    single { LotteryLogUseCase() }
    single { LotteryLogViewModel(get()) }
    single { PossibilityScreenViewModel(get(), get()) }
    single { PlusMinusViewModel(get(), get()) }
}